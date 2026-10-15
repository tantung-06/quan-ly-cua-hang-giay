package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.ChiTietPhieuNhap;
import config.DatabaseConnection;

public class ChiTietPhieuNhapDAO {
    public ArrayList<ChiTietPhieuNhap> getByPhieuNhap(String maPhieuNhap) {
        ArrayList<ChiTietPhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getString("maBienThe"),
                        rs.getInt("soLuong"),
                        rs.getDouble("giaNhap"),
                        rs.getDouble("thanhTien"));
                ds.add(ctpn);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public ChiTietPhieuNhap getById(String maPhieuNhap, String maBienThe) {
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieuNhap=? AND maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.setString(2, maBienThe);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new ChiTietPhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getString("maBienThe"),
                        rs.getInt("soLuong"),
                        rs.getDouble("giaNhap"),
                        rs.getDouble("thanhTien"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(ChiTietPhieuNhap ctpn) {
        String sql = "INSERT INTO ChiTietPhieuNhap(maPhieuNhap,maBienThe,soLuong,giaNhap,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ctpn.getMaPhieuNhap());
            ps.setString(2, ctpn.getMaBienThe());
            ps.setInt(3, ctpn.getSoLuong());
            ps.setDouble(4, ctpn.getGiaNhap());
            ps.setDouble(5, ctpn.getThanhTien());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBatch(ArrayList<ChiTietPhieuNhap> ds) {
        String sql = "INSERT INTO ChiTietPhieuNhap(maPhieuNhap,maBienThe,soLuong,giaNhap,thanhTien) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (ChiTietPhieuNhap obj : ds) {
                ps.setString(1, obj.getMaPhieuNhap());
                ps.setString(2, obj.getMaBienThe());
                ps.setInt(3, obj.getSoLuong());
                ps.setDouble(4, obj.getGiaNhap());
                ps.setDouble(5, obj.getThanhTien());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChiTietPhieuNhap ctpn) {
        String sql = "UPDATE ChiTietPhieuNhap SET soLuong=?,giaNhap=?,thanhTien=? WHERE maPhieuNhap=? AND maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ctpn.getSoLuong());
            ps.setDouble(2, ctpn.getGiaNhap());
            ps.setDouble(3, ctpn.getThanhTien());
            ps.setString(4, ctpn.getMaPhieuNhap());
            ps.setString(5, ctpn.getMaBienThe());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuNhap, String maBienThe) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPhieuNhap=? AND maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ps.setString(2, maBienThe);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByPhieuNhap(String maPhieuNhap) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            return ps.executeUpdate() > 0;
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
