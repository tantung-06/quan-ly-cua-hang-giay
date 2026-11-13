package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import config.DatabaseConnection.ConnectionFactory;
import dto.LichSuDiem;

public class LichSuDiemDAO {
    private final ConnectionFactory connections;

    public LichSuDiemDAO() {
        this(DatabaseConnection::getConnection);
    }

    public LichSuDiemDAO(ConnectionFactory connections) {
        this.connections = java.util.Objects.requireNonNull(connections);
    }

    public void insert(Connection connection, LichSuDiem entry) throws SQLException {
        String sql = "INSERT INTO LichSuDiem(maKhachHang,maHoaDon,loai,soDiem,diemSau) VALUES(?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entry.getMaKhachHang());
            statement.setString(2, entry.getMaHoaDon());
            statement.setString(3, entry.getLoai());
            statement.setInt(4, entry.getSoDiem());
            statement.setInt(5, entry.getDiemSau());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Không thể lưu lịch sử điểm.");
            }
        }
    }

    public ArrayList<LichSuDiem> getByKhachHang(String customerId) {
        String sql = "SELECT * FROM LichSuDiem WHERE maKhachHang=? ORDER BY maLichSu DESC";
        ArrayList<LichSuDiem> entries = new ArrayList<>();
        try (Connection connection = connections.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    entries.add(new LichSuDiem(rows.getLong("maLichSu"), rows.getString("maKhachHang"),
                            rows.getString("maHoaDon"), rows.getString("loai"), rows.getInt("soDiem"),
                            rows.getInt("diemSau"), rows.getTimestamp("thoiGian").toLocalDateTime()));
                }
            }
            return entries;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải lịch sử điểm. Kiểm tra migration cơ sở dữ liệu.", ex);
        }
    }
}