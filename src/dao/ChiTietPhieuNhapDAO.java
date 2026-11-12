package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.ChiTietPhieuNhap;

public class ChiTietPhieuNhapDAO {
    public ArrayList<ChiTietPhieuNhap> getByPhieuNhap(String maPhieuNhap) {
        ArrayList<ChiTietPhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
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

    public ChiTietPhieuNhap getById(String maPhieuNhap, String maSanPham) {
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieuNhap=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
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

    public boolean insert(ChiTietPhieuNhap ctpn) {
        String sql = "INSERT INTO ChiTietPhieuNhap(maPhieuNhap,maSanPham,soLuong,giaNhap,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ctpn.getMaPhieuNhap());
            ps.setString(2, ctpn.getMaSanPham());
            ps.setInt(3, ctpn.getSoLuong());
            ps.setDouble(4, ctpn.getGiaNhap());
            ps.setDouble(5, ctpn.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBatch(ArrayList<ChiTietPhieuNhap> ds) {
        String sql = "INSERT INTO ChiTietPhieuNhap(maPhieuNhap,maSanPham,soLuong,giaNhap,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (ChiTietPhieuNhap obj : ds) {
                        ps.setString(1, obj.getMaPhieuNhap());
                        ps.setString(2, obj.getMaSanPham());
                        ps.setInt(3, obj.getSoLuong());
                        ps.setDouble(4, obj.getGiaNhap());
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

    public boolean update(ChiTietPhieuNhap ctpn) {
        String sql = "UPDATE ChiTietPhieuNhap SET soLuong=?,giaNhap=?,thanhTien=? WHERE maPhieuNhap=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ctpn.getSoLuong());
            ps.setDouble(2, ctpn.getGiaNhap());
            ps.setDouble(3, ctpn.getThanhTien());
            ps.setString(4, ctpn.getMaPhieuNhap());
            ps.setString(5, ctpn.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuNhap, String maSanPham) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPhieuNhap=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByPhieuNhap(String maPhieuNhap) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChiTietPhieuNhap mapRow(ResultSet rs) throws SQLException {
        return new ChiTietPhieuNhap(
                rs.getString("maPhieuNhap"),
                rs.getString("maSanPham"),
                rs.getInt("soLuong"),
                rs.getDouble("giaNhap"),
                rs.getDouble("thanhTien"));
    }
}