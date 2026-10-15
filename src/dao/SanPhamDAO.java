package dao;

import java.sql.*;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.SanPham;

public class SanPhamDAO {
    public ArrayList<SanPham> getAll() {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM SanPham ORDER BY maSanPham ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham s = new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
                ds.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<SanPham> getByTrangThai(String trangThai) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE trangThai=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham s = new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
                ds.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<SanPham> getByThuongHieu(String maThuongHieu) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE maThuongHieu=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThuongHieu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham s = new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
                ds.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<SanPham> getByLoaiGiay(String maLoaiGiay) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE maLoaiGiay=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLoaiGiay);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham s = new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
                ds.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public SanPham getById(String maSanPham) {
        String sql = "SELECT * FROM SanPham WHERE maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    


    public ArrayList<SanPham> search(String keyword) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE tenSanPham LIKE ?";
        String kw = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham s = new SanPham(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("maLoaiGiay"),
                        rs.getString("maThuongHieu"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("trangThai"));
                ds.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(SanPham s) {
        String sql = "INSERT INTO SanPham(maSanPham,tenSanPham,maLoaiGiay,maThuongHieu,giaBan,soLuongTon,trangThai) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getMaSanPham());
            ps.setString(2, s.getTenSanPham());
            ps.setString(3, s.getMaLoaiGiay());
            ps.setString(4, s.getMaThuongHieu());
            ps.setDouble(5, s.getGiaBan());
            ps.setInt(6, s.getSoLuongTon());
            ps.setString(7, s.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SanPham s) {
        String sql = "UPDATE SanPham SET tenSanPham=?,maLoaiGiay=?,maThuongHieu=?,giaBan=?,soLuongTon=?,trangThai=? WHERE maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getTenSanPham());
            ps.setString(2, s.getMaLoaiGiay());
            ps.setString(3, s.getMaThuongHieu());
            ps.setDouble(4, s.getGiaBan());
            ps.setInt(5, s.getSoLuongTon());
            ps.setString(6, s.getTrangThai());
            ps.setString(7, s.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(String maSanPham) {   
        String sql = "DELETE FROM SanPham WHERE maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTrangThai(String maSanPham, String trangThai) {
        String sql = "UPDATE SanPham SET trangThai=? WHERE maSanPham=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaSanPham() {
        String sql = "SELECT maSanPham FROM SanPham ORDER BY maSanPham DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("maSanPham");
                int num = Integer.parseInt(last.replaceAll("[^0-9]", ""));
                return String.format("S%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "S001";
    }
}
