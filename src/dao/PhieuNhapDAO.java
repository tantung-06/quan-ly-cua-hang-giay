package dao;

import java.sql.*;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.PhieuNhap;

public class PhieuNhapDAO {
    public ArrayList<PhieuNhap> getAll() {
        ArrayList<PhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap ORDER BY ngayNhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maNCC"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<PhieuNhap> getByTrangThai(String trangThai) {
        ArrayList<PhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE trangThai=? ORDER BY ngayNhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maNCC"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<PhieuNhap> getByNhanVien(String maNhanVien) {
        ArrayList<PhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE maNhanVien=? ORDER BY ngayNhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maNCC"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public PhieuNhap getById(String maPhieuNhap) {
        String sql = "SELECT * FROM PhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PhieuNhap(
                        rs.getString("maPhieuNhap"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maNCC"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(PhieuNhap pn) {
        String sql = "INSERT INTO PhieuNhap(maPhieuNhap,ngayNhap,maNhanVien,maNCC,tongTien,trangThai) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pn.getMaPhieuNhap());
            ps.setDate(2, Date.valueOf(pn.getNgayNhap()));
            ps.setString(3, pn.getMaNhanVien());
            ps.setString(4, pn.getMaNCC());
            ps.setDouble(5, pn.getTongTien());
            ps.setString(6, pn.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PhieuNhap pn) {
        String sql = "UPDATE PhieuNhap SET ngayNhap=?,maNhanVien=?,maNCC=?,tongTien=?,trangThai=? WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(pn.getNgayNhap()));
            ps.setString(2, pn.getMaNhanVien());
            ps.setString(3, pn.getMaNCC());
            ps.setDouble(4, pn.getTongTien());
            ps.setString(5, pn.getTrangThai());
            ps.setString(6, pn.getMaPhieuNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String maPhieuNhap, String trangThai) {
        String sql = "UPDATE PhieuNhap SET trangThai=? WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maPhieuNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuNhap) {
        String sql = "DELETE FROM PhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaPhieuNhap() {
        String sql = "SELECT maPhieuNhap FROM PhieuNhap ORDER BY maPhieuNhap DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maPhieuNhap").replaceAll("[^0-9]", ""));
                return String.format("PN%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PN001";
    }
}
