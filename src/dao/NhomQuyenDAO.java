package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.NhomQuyen;

public class NhomQuyenDAO {
    public ArrayList<NhomQuyen> getAll() {
        ArrayList<NhomQuyen> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhomQuyen ORDER BY maNhomQuyen ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public NhomQuyen getByID(String maNhomQuyen) {
        String sql = "SELECT * FROM NhomQuyen WHERE maNhomQuyen=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhomQuyen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(NhomQuyen nq) {
        String sql = "INSERT INTO NhomQuyen(maNhomQuyen,tenNhomQuyen,quyenHan) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePermissionColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nq.getMaNhomQuyen());
                ps.setString(2, nq.getTenNhomQuyen());
                ps.setString(3, nq.getQuyenHan());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Không thể lưu phân quyền. Kiểm tra kết nối và quyền cập nhật cơ sở dữ liệu.", e);
        }
    }

    public boolean update(NhomQuyen nq) {
        String sql = "UPDATE NhomQuyen SET tenNhomQuyen=?,quyenHan=? WHERE maNhomQuyen=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensurePermissionColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nq.getTenNhomQuyen());
                ps.setString(2, nq.getQuyenHan());
                ps.setString(3, nq.getMaNhomQuyen());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Không thể lưu phân quyền. Kiểm tra kết nối và quyền cập nhật cơ sở dữ liệu.", e);
        }
    }

    public boolean delete(String maNhomQuyen) {
        String sql = "DELETE FROM NhomQuyen WHERE maNhomQuyen=? AND maNhomQuyen<>'NQ001'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhomQuyen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaNhomQuyen() {
        String sql = "SELECT maNhomQuyen FROM NhomQuyen ORDER BY maNhomQuyen DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maNhomQuyen").replaceAll("[^0-9]", ""));
                return String.format("NQ%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NQ001";
    }

    private NhomQuyen mapRow(ResultSet rs) throws SQLException {
        return new NhomQuyen(
                rs.getString("maNhomQuyen"),
                rs.getString("tenNhomQuyen"),
                hasPermissionColumn(rs.getMetaData()) ? rs.getString("quyenHan") : null);
    }

    private boolean hasPermissionColumn(ResultSetMetaData columns) throws SQLException {
        for (int i = 1; i <= columns.getColumnCount(); i++) {
            if ("quyenHan".equalsIgnoreCase(columns.getColumnLabel(i)))
                return true;
        }
        return false;
    }

    private void ensurePermissionColumn(Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM NhomQuyen WHERE 1=0");
                ResultSet rows = query.executeQuery()) {
            if (hasPermissionColumn(rows.getMetaData()))
                return;
        }
        try (java.sql.Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE NhomQuyen ADD COLUMN quyenHan TEXT NULL");
        } catch (SQLException ex) {
            // A concurrent first save may already have added the column.
            if (ex.getErrorCode() != 1060)
                throw ex;
        }
    }
}