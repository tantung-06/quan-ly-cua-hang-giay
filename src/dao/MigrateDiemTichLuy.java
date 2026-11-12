package dao;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.DatabaseConnection;

/**
 * Upgrade loyalty storage while preserving balances, receipts and existing
 * ledger entries.
 */
public final class MigrateDiemTichLuy {
    private MigrateDiemTichLuy() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (isInstalled(connection)) {
                System.out.println("Migration điểm tích lũy đã được cài đặt.");
                return;
            }
            Map<String, String> before = snapshot(connection);
            Path backup = Paths.get("out", "loyalty-before-" + System.currentTimeMillis() + ".txt");
            Files.createDirectories(backup.getParent());
            String backupData = before.entrySet() + "\nHistory: " + snapshotHistory(connection).entrySet()
                    + "\nRemoved columns: " + snapshotObsoleteColumns(connection).entrySet();
            Files.write(backup, backupData.getBytes(StandardCharsets.UTF_8));
            migrate(connection);
            System.out.println("Đã cập nhật điểm tích lũy; giữ nguyên " + before.size()
                    + " số dư khách hàng/phiếu cũ. Bản đối chiếu: " + backup);
        }
    }

    public static void migrate(Connection connection) throws Exception {
        if (isInstalled(connection)) {
            return;
        }
        if (hasColumn(connection, "LichSuDiem", "maLichSu")) {
            validateLedger(connection);
        }
        Map<String, String> before = snapshot(connection);
        Map<String, String> existingHistory = snapshotHistory(connection);
        String script = readMigrationScript();
        try (Statement statement = connection.createStatement()) {
            for (String sql : script.replaceAll("(?m)--[^\\r\\n]*", "").split(";")) {
                sql = sql.trim();
                if (sql.startsWith("ALTER TABLE")) {
                    String[] parts = sql.split("\\s+", 4);
                    for (String addition : splitAdditions(parts[3])) {
                        String[] tokens = addition.trim().split("\\s+", 4);
                        boolean exists = "COLUMN".equals(tokens[1])
                                ? hasColumn(connection, parts[2], tokens[2])
                                : hasConstraint(connection, parts[2], tokens[2]);
                        if (!exists) {
                            statement.execute("ALTER TABLE " + parts[2] + " " + addition);
                        }
                    }
                } else if (sql.startsWith("CREATE TABLE LichSuDiem")) {
                    if (!hasColumn(connection, "LichSuDiem", "maLichSu")) {
                        statement.execute(sql);
                    }
                } else if (!sql.isEmpty()) {
                    statement.execute(sql);
                }
            }
        }
        removeObsoleteColumns(connection);
        if (!before.equals(snapshot(connection))
                || !snapshotHistory(connection).entrySet().containsAll(existingHistory.entrySet())
                || !isInstalled(connection)) {
            throw new IllegalStateException("Dữ liệu sau migration không khớp bản đối chiếu trước migration.");
        }
    }

    /** Derive upgrades from the same schema used for a new installation. */
    private static String readMigrationScript() throws java.io.IOException {
        String schema = new String(Files.readAllBytes(Paths.get("database/quanlycuahanggiay.sql")),
                StandardCharsets.UTF_8);
        StringBuilder migration = new StringBuilder();
        appendAlter(migration, schema, "PhieuXuat", "diemSuDung", "diemDuocCong", "diemDaXuLy",
                "CK_PhieuXuat_Diem", "CK_PhieuXuat_ThucTra");
        migration.append(createTable(schema, "LichSuDiem").group()).append('\n');
        Matcher openingBalance = Pattern.compile("(?s)INSERT INTO LichSuDiem\\s*\\([^;]+;").matcher(schema);
        if (!openingBalance.find()) {
            throw new IllegalStateException("Thiếu câu lệnh số dư đầu kỳ trong quanlycuahanggiay.sql.");
        }
        return migration.append(openingBalance.group()).toString();
    }

    private static void appendAlter(StringBuilder migration, String schema, String table, String... names) {
        Set<String> missing = new LinkedHashSet<>(Arrays.asList(names));
        List<String> additions = new ArrayList<>();
        for (String definition : splitAdditions(createTable(schema, table).group(1))) {
            String[] tokens = definition.split("\\s+", 3);
            boolean constraint = "CONSTRAINT".equals(tokens[0]);
            String name = constraint ? tokens[1] : tokens[0];
            if (missing.remove(name)) {
                additions.add("ADD " + (constraint ? "" : "COLUMN ") + definition);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Thiếu cấu trúc " + table + ": " + missing);
        }
        migration.append("ALTER TABLE ").append(table).append(' ')
                .append(String.join(",\n", additions)).append(";\n");
    }

    private static Matcher createTable(String schema, String table) {
        Matcher matcher = Pattern.compile("(?s)CREATE TABLE " + Pattern.quote(table)
                + "\\s*\\((.*?)\\)\\s*ENGINE=InnoDB\\s*;").matcher(schema);
        if (!matcher.find()) {
            throw new IllegalStateException("Thiếu định nghĩa bảng " + table + " trong quanlycuahanggiay.sql.");
        }
        return matcher;
    }

    public static boolean isInstalled(Connection connection) throws SQLException {
        if (installedParts(connection) != 4
                || hasColumn(connection, "KhachHang", "diemNo")
                || hasColumn(connection, "LichSuDiem", "noSau")
                || !hasConstraint(connection, "LichSuDiem", "CK_LichSuDiem_SoDiem")
                || !hasConstraint(connection, "PhieuXuat", "CK_PhieuXuat_Diem")
                || !hasConstraint(connection, "PhieuXuat", "CK_PhieuXuat_ThucTra")) {
            return false;
        }
        validateLedger(connection);
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery(
                        "SELECT COUNT(*) FROM KhachHang k WHERE NOT EXISTS (SELECT 1 FROM LichSuDiem h WHERE h.maKhachHang=k.maKhachHang)")) {
            rows.next();
            return rows.getInt(1) == 0;
        }
    }

    private static List<String> splitAdditions(String text) {
        List<String> additions = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '(')
                depth++;
            if (ch == ')')
                depth--;
            if (ch == ',' && depth == 0) {
                additions.add(text.substring(start, i).trim());
                start = i + 1;
            }
        }
        additions.add(text.substring(start).trim());
        return additions;
    }

    private static void validateLedger(Connection connection) throws SQLException {
        for (String column : new String[] { "maLichSu", "maKhachHang", "maPhieuXuat", "loai", "soDiem", "diemSau",
                "thoiGian" }) {
            if (!hasColumn(connection, "LichSuDiem", column)) {
                throw new IllegalStateException(
                        "Bảng LichSuDiem hiện hữu thiếu cột " + column + ". Không thay đổi dữ liệu.");
            }
        }
        for (String constraint : new String[] { "UQ_LichSuDiem_PhieuLoai", "FK_LichSuDiem_KhachHang",
                "FK_LichSuDiem_PhieuXuat" }) {
            if (!hasConstraint(connection, "LichSuDiem", constraint)) {
                throw new IllegalStateException(
                        "Bảng LichSuDiem hiện hữu thiếu ràng buộc " + constraint + ". Không thay đổi dữ liệu.");
            }
        }
    }

    private static boolean hasConstraint(Connection connection, String table, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA=DATABASE() AND TABLE_NAME=? AND CONSTRAINT_NAME=?")) {
            statement.setString(1, table);
            statement.setString(2, name);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        }
    }

    private static boolean hasColumn(Connection connection, String table, String column) throws SQLException {
        try (ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), null, table, column)) {
            return columns.next();
        }
    }

    private static int installedParts(Connection connection) throws SQLException {
        String[][] parts = { { "PhieuXuat", "diemSuDung" },
                { "PhieuXuat", "diemDuocCong" }, { "PhieuXuat", "diemDaXuLy" }, { "LichSuDiem", "maLichSu" } };
        int count = 0;
        for (String[] part : parts) {
            try (ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), null, part[0],
                    part[1])) {
                if (columns.next()) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void removeObsoleteColumns(Connection connection) throws Exception {
        List<String> changes = new ArrayList<>();
        if (hasConstraint(connection, "KhachHang", "CK_KhachHang_DiemNo")) {
            changes.add("DROP CHECK CK_KhachHang_DiemNo");
        }
        if (hasColumn(connection, "KhachHang", "diemNo")) {
            changes.add("DROP COLUMN diemNo");
        }
        try (Statement statement = connection.createStatement()) {
            if (!changes.isEmpty()) {
                statement.execute("ALTER TABLE KhachHang " + String.join(",", changes));
            }
            changes.clear();
            boolean obsoleteColumn = hasColumn(connection, "LichSuDiem", "noSau");
            boolean constraintExists = hasConstraint(connection, "LichSuDiem", "CK_LichSuDiem_SoDiem");
            if (obsoleteColumn) {
                if (constraintExists) {
                    changes.add("DROP CHECK CK_LichSuDiem_SoDiem");
                }
                changes.add("DROP COLUMN noSau");
            }
            if (obsoleteColumn || !constraintExists) {
                String schema = new String(Files.readAllBytes(Paths.get("database/quanlycuahanggiay.sql")),
                        StandardCharsets.UTF_8);
                String definition = splitAdditions(createTable(schema, "LichSuDiem").group(1)).stream()
                        .filter(value -> value.startsWith("CONSTRAINT CK_LichSuDiem_SoDiem "))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Thiếu ràng buộc số dư điểm trong SQL."));
                changes.add("ADD " + definition);
            }
            if (!changes.isEmpty()) {
                statement.execute("ALTER TABLE LichSuDiem " + String.join(",", changes));
            }
        }
    }

    private static Map<String, String> snapshotObsoleteColumns(Connection connection) throws SQLException {
        Map<String, String> values = new LinkedHashMap<>();
        if (hasColumn(connection, "KhachHang", "diemNo")) {
            try (Statement statement = connection.createStatement();
                    ResultSet rows = statement
                            .executeQuery("SELECT maKhachHang,diemNo FROM KhachHang ORDER BY maKhachHang")) {
                while (rows.next())
                    values.put("KhachHang:" + rows.getString(1), rows.getString(2));
            }
        }
        if (hasColumn(connection, "LichSuDiem", "noSau")) {
            try (Statement statement = connection.createStatement();
                    ResultSet rows = statement
                            .executeQuery("SELECT maLichSu,noSau FROM LichSuDiem ORDER BY maLichSu")) {
                while (rows.next())
                    values.put("LichSuDiem:" + rows.getString(1), rows.getString(2));
            }
        }
        return values;
    }

    private static Map<String, String> snapshotHistory(Connection connection) throws SQLException {
        Map<String, String> values = new LinkedHashMap<>();
        if (!hasColumn(connection, "LichSuDiem", "maLichSu"))
            return values;
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery(
                        "SELECT maLichSu,maKhachHang,maPhieuXuat,loai,soDiem,diemSau,thoiGian FROM LichSuDiem ORDER BY maLichSu")) {
            while (rows.next()) {
                values.put(rows.getString(1), rows.getString(2) + ":" + rows.getString(3) + ":" + rows.getString(4)
                        + ":" + rows.getString(5) + ":" + rows.getString(6) + ":" + rows.getString(7));
            }
        }
        return values;
    }

    private static Map<String, String> snapshot(Connection connection) throws SQLException {
        Map<String, String> snapshot = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement
                        .executeQuery("SELECT maKhachHang,diemTichLuy FROM KhachHang ORDER BY maKhachHang")) {
            while (rows.next()) {
                snapshot.put("KH:" + rows.getString(1), rows.getString(2));
            }
        }
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement
                        .executeQuery("SELECT maPhieuXuat,tongTien,trangThai FROM PhieuXuat ORDER BY maPhieuXuat")) {
            while (rows.next()) {
                snapshot.put("HD:" + rows.getString(1), rows.getString(2) + ":" + rows.getString(3));
            }
        }
        return snapshot;
    }
}