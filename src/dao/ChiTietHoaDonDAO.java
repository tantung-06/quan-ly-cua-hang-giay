package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.ChiTietHoaDon;

public class ChiTietHoaDonDAO {
    public ArrayList<ChiTietHoaDon> getByHoaDon(String maHoaDon) {
        ArrayList<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ChiTietHoaDon getById(String maHoaDon, String maSanPham) {
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maSanPham);
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

    public boolean insert(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO ChiTietHoaDon(maHoaDon,maSanPham,soLuong,donGia,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cthd.getMaHoaDon());
            ps.setString(2, cthd.getMaSanPham());
            ps.setInt(3, cthd.getSoLuong());
            ps.setDouble(4, cthd.getDonGia());
            ps.setDouble(5, cthd.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBatch(ArrayList<ChiTietHoaDon> ds) {
        String sql = "INSERT INTO ChiTietHoaDon(maHoaDon,maSanPham,soLuong,donGia,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (ChiTietHoaDon obj : ds) {
                        ps.setString(1, obj.getMaHoaDon());
                        ps.setString(2, obj.getMaSanPham());
                        ps.setInt(3, obj.getSoLuong());
                        ps.setDouble(4, obj.getDonGia());
                        ps.setDouble(5, obj.getThanhTien());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                e.printStackTrace();
                return false;
            } finally {
                try {
                    conn.setAutoCommit(previousAutoCommit);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChiTietHoaDon cthd) {
        String sql = "UPDATE ChiTietHoaDon SET soLuong=?,donGia=?,thanhTien=? WHERE maHoaDon=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cthd.getSoLuong());
            ps.setDouble(2, cthd.getDonGia());
            ps.setDouble(3, cthd.getThanhTien());
            ps.setString(4, cthd.getMaHoaDon());
            ps.setString(5, cthd.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maHoaDon, String maSanPham) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByHoaDon(String maHoaDon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChiTietHoaDon mapRow(ResultSet rs) throws SQLException {
        return new ChiTietHoaDon(
                rs.getString("maHoaDon"),
                rs.getString("maSanPham"),
                rs.getInt("soLuong"),
                rs.getDouble("donGia"),
                rs.getDouble("thanhTien"));
    }
}