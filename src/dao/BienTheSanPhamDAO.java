package dao;

import java.sql.*;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.BienTheSanPham;

public class BienTheSanPhamDAO {
    public ArrayList<BienTheSanPham> getAll() {
        ArrayList<BienTheSanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM BienTheSanPham ORDER BY maBienThe ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BienTheSanPham btsp = new BienTheSanPham(
                    rs.getString("maBienThe"),
                    rs.getString("maSanPham"),
                    rs.getString("maMau"),
                    rs.getString("maKichThuong"),
                    rs.getDouble("giaBan"),
                    rs.getInt("soLuongTon"),
                    rs.getString("hinhAnh"),
                    rs.getString("trangThai"));
                ds.add(btsp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public BienTheSanPham getById(String maBienThe) {
        String sql = "SELECT * FROM BienTheSanPham WHERE maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBienThe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BienTheSanPham(
                        rs.getString("maBienThe"),
                        rs.getString("maSanPham"),
                        rs.getString("maMau"),
                        rs.getString("maKichThuong"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon"),
                        rs.getString("hinhAnh"),
                        rs.getString("trangThai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(BienTheSanPham b) {
        String sql = "INSERT INTO BienTheSanPham(maBienThe,maSanPham,maMau,maKichThuong,giaBan,soLuongTon,hinhAnh,trangThai) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getMaBienThe());
            ps.setString(2, b.getMaSanPham());
            ps.setString(3, b.getMaMau());
            ps.setString(4, b.getMaKichThuoc());
            ps.setDouble(5, b.getGiaBan());
            ps.setInt(6, b.getSoLuongTon());
            ps.setString(7, b.getHinhAnh());
            ps.setString(8, b.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(BienTheSanPham b) {
        String sql = "UPDATE BienTheSanPham SET maSanPham=?,maMau=?,maKichThuong=?,giaBan=?,soLuongTon=?,hinhAnh=?,trangThai=? "
                + "WHERE maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getMaSanPham());
            ps.setString(2, b.getMaMau());
            ps.setString(3, b.getMaKichThuoc());
            ps.setDouble(4, b.getGiaBan());
            ps.setInt(5, b.getSoLuongTon());
            ps.setString(6, b.getHinhAnh());
            ps.setString(7, b.getTrangThai());
            ps.setString(8, b.getMaBienThe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSoLuongTon(String maBienThe, int soLuong) {
        String sql = "UPDATE BienTheSanPham SET "
                + "trangThai = CASE "
                + "  WHEN trangThai NOT IN ('CON_HANG','HET_HANG') THEN trangThai "
                + "  WHEN soLuongTon + ? <= 0 THEN 'HET_HANG' "
                + "  ELSE 'CON_HANG' END, "
                + "soLuongTon = soLuongTon + ? "
                + "WHERE maBienThe = ? AND soLuongTon + ? >= 0";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, soLuong);
            ps.setString(3, maBienThe);
            ps.setInt(4, soLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String maBienThe, String trangThai) {
        String sql = "UPDATE BienTheSanPham SET trangThai=? WHERE maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maBienThe);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThaiBySanPham(String maSanPham, String trangThai) {
        String sql = "UPDATE BienTheSanPham SET trangThai=? WHERE maSanPham=?";
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

    public boolean delete(String maBienThe) {
        String sql = "DELETE FROM BienTheSanPham WHERE maBienThe=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBienThe);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaBienThe() {
        String sql = "SELECT maBienThe FROM BienTheSanPham ORDER BY maBienThe DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("maBienThe");
                int num = Integer.parseInt(last.replaceAll("[^0-9]", ""));
                return String.format("BT%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "BT001";
    }
}