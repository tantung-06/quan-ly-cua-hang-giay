package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.ChiTietPhieuXuat;

public class ChiTietPhieuXuatDAO {
    public ArrayList<ChiTietPhieuXuat> getByPhieuXuat(String maPhieuXuat) {
        ArrayList<ChiTietPhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuXuat WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
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

    public ChiTietPhieuXuat getById(String maPhieuXuat, String maSanPham) {
        String sql = "SELECT * FROM ChiTietPhieuXuat WHERE maPhieuXuat=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
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

    public boolean insert(ChiTietPhieuXuat ctpx) {
        String sql = "INSERT INTO ChiTietPhieuXuat(maPhieuXuat,maSanPham,soLuong,donGia,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ctpx.getMaPhieuXuat());
            ps.setString(2, ctpx.getMaSanPham());
            ps.setInt(3, ctpx.getSoLuong());
            ps.setDouble(4, ctpx.getDonGia());
            ps.setDouble(5, ctpx.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBatch(ArrayList<ChiTietPhieuXuat> ds) {
        String sql = "INSERT INTO ChiTietPhieuXuat(maPhieuXuat,maSanPham,soLuong,donGia,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (ChiTietPhieuXuat obj : ds) {
                        ps.setString(1, obj.getMaPhieuXuat());
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

    public boolean update(ChiTietPhieuXuat ctpx) {
        String sql = "UPDATE ChiTietPhieuXuat SET soLuong=?,donGia=?,thanhTien=? WHERE maPhieuXuat=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ctpx.getSoLuong());
            ps.setDouble(2, ctpx.getDonGia());
            ps.setDouble(3, ctpx.getThanhTien());
            ps.setString(4, ctpx.getMaPhieuXuat());
            ps.setString(5, ctpx.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuXuat, String maSanPham) {
        String sql = "DELETE FROM ChiTietPhieuXuat WHERE maPhieuXuat=? AND maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByPhieuXuat(String maPhieuXuat) {
        String sql = "DELETE FROM ChiTietPhieuXuat WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChiTietPhieuXuat mapRow(ResultSet rs) throws SQLException {
        return new ChiTietPhieuXuat(
                rs.getString("maPhieuXuat"),
                rs.getString("maSanPham"),
                rs.getInt("soLuong"),
                rs.getDouble("donGia"),
                rs.getDouble("thanhTien"));
    }
}