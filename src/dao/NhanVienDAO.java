package dao;

import java.sql.*;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.NhanVien;

public class NhanVienDAO {
    public ArrayList<NhanVien> getAll() {
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNhanVien ASC";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                NhanVien nv = new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("trangThai")
                );
                ds.add(nv);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }
    
    public NhanVien getById(String maNhanVien) {
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("trangThai"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public NhanVien getByTaiKhoan(String maTaiKhoan) {
        String sql = "SELECT * FROM NhanVien WHERE maTaiKhoan=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("trangThai"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<NhanVien> search(String keyword){
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE ho LIKE ? OR ten LIKE ? OR soDienThoai LIKE ? OR email LIKE ?";
        String kw = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                NhanVien nv = new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("trangThai"));
                ds.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien(maNhanVien,ho,ten,soDienThoai,diaChi,email,maTaiKhoan,trangThai) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHo());
            ps.setString(3, nv.getTen());
            ps.setString(4, nv.getSoDienThoai());
            ps.setString(5, nv.getDiaChi());
            ps.setString(6, nv.getEmail());
            ps.setString(7, nv.getMaTaiKhoan());
            ps.setString(8, nv.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET ho=?,ten=?,soDienThoai=?,diaChi=?,email=?,maTaiKhoan=?,trangThai=? WHERE maNhanVien=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHo());
            ps.setString(2, nv.getTen());
            ps.setString(3, nv.getSoDienThoai());
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getEmail());
            ps.setString(6, nv.getMaTaiKhoan());
            ps.setString(7, nv.getTrangThai());
            ps.setString(8, nv.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(String maNhanVien) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<NhanVien> getByTrangThai(String trangThai){
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE trangThai=?";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                NhanVien nv = new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("ho"),
                        rs.getString("ten"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getString("maTaiKhoan"),
                        rs.getString("trangThai"));
                ds.add(nv);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public boolean updateTrangThai(String maNhanVien, String trangThai){
        String sql = "UPDATE NhanVien SET trangThai=? WHERE maNhanVien=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, trangThai);
            ps.setString(2, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public String generateMaNhanVien() {
        String sql = "SELECT maNhanVien FROM NhanVien ORDER BY maNhanVien DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maNhanVien").replaceAll("[^0-9]", ""));
                return String.format("NV%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NV001";
    }
}
