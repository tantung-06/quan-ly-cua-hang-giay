package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.ChiTietPhieuXuat;
import config.DatabaseConnection;

public class ChiTietPhieuXuatDAO {
    public ArrayList<ChiTietPhieuXuat> getByPhieuXuat(String maPhieuXuat) {
        ArrayList<ChiTietPhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuXuat WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuXuat ctpx = new ChiTietPhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getString("maSanPham"),
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia"),
                        rs.getDouble("thanhTien"));
                ds.add(ctpx);
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
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ChiTietPhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getString("maSanPham"),
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia"),
                        rs.getDouble("thanhTien"));
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
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (ChiTietPhieuXuat obj : ds) {
                ps.setString(1, obj.getMaPhieuXuat());
                ps.setString(2, obj.getMaSanPham());
                ps.setInt(3, obj.getSoLuong());
                ps.setDouble(4, obj.getDonGia());
                ps.setDouble(5, obj.getThanhTien());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
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
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
