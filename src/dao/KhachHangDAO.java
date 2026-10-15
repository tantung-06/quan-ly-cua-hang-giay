package dao;

import java.sql.*;
import java.util.ArrayList;

import dto.KhachHang;
import config.DatabaseConnection;

public class KhachHangDAO {
    public ArrayList<KhachHang> getAll() {
        ArrayList<KhachHang> ds = new ArrayList();
        String sql = "SELECT * FROM KhachHang ORDER BY maKhachHang ASC";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("trangThai"));
                ds.add(kh);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return ds;
    }

    public ArrayList<KhachHang> getByTrangThai(String trangThai){
        ArrayList<KhachHang> ds = new ArrayList();
        String sql = "SELECT * FROM KhachHang WHERE trangThai=?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("trangThai"));
                ds.add(kh);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public KhachHang getById(String maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang=?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("trangThai"));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<KhachHang> search(String keyword){
        ArrayList<KhachHang> ds = new ArrayList();
        String sql = "SELECT * FROM KhachHang WHERE ho LIKE ? OR ten LIKE ? OR soDienThoai LIKE ? OR email LIKE ?";
        String kw = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("trangThai"));
                ds.add(kh);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKhachHang,ho,ten,soDienThoai,diaChi,email,diemTichLuy,trangThai) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHo());
            ps.setString(3, kh.getTen());
            ps.setString(4, kh.getSoDienThoai());
            ps.setString(5, kh.getDiaChi());
            ps.setString(6, kh.getEmail());
            ps.setInt(7, kh.getDiemTichLuy());
            ps.setString(8, kh.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhachHang kh){
        String sql = "UPDATE KhachHang SET ho=?,ten=?,soDienThoai=?,diaChi=?,email=?,diemTichLuy=?,trangThai=? WHERE maKhachHang=?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getHo());
            ps.setString(2, kh.getTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getEmail());
            ps.setInt(6, kh.getDiemTichLuy());
            ps.setString(7, kh.getTrangThai());
            ps.setString(8, kh.getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String maKhachHang, String trangThai) {
        String sql = "UPDATE KhachHang SET trangThai=? WHERE maKhachHang=?";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maKhachHang);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maKhachHang){
        String sql = "DELETE FROM KhachHang WHERE maKhachHang=?";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public String generateMaKhachHang() {
        String sql = "SELECT maKhachHang FROM KhachHang ORDER BY maKhachHang DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maKhachHang").replaceAll("[^0-9]", ""));
                return String.format("KH%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KH001";
    }
}