package dao;

import java.sql.*;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.PhieuXuat;

public class PhieuXuatDAO {
    public ArrayList<PhieuXuat> getAll() {
        ArrayList<PhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuXuat ORDER BY ngayLap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuXuat px = new PhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maKhachHang"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(px);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<PhieuXuat> getByTrangThai(String trangThai) {
        ArrayList<PhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuXuat WHERE trangThai=? ORDER BY ngayLap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuXuat px = new PhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maKhachHang"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(px);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<PhieuXuat> getByKhachHang(String maKhachHang) {
        ArrayList<PhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuXuat WHERE maKhachHang=? ORDER BY ngayLap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuXuat px = new PhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maKhachHang"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(px);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<PhieuXuat> getByNhanVien(String maNhanVien) {
        ArrayList<PhieuXuat> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuXuat WHERE maNhanVien=? ORDER BY ngayLap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuXuat px = new PhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maKhachHang"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                ds.add(px);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public PhieuXuat getById(String maPhieuXuat) {
        String sql = "SELECT * FROM PhieuXuat WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PhieuXuat(
                        rs.getString("maPhieuXuat"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getString("maNhanVien"),
                        rs.getString("maKhachHang"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(PhieuXuat px) {
        String sql = "INSERT INTO PhieuXuat(maPhieuXuat,ngayLap,maNhanVien,maKhachHang,maKhuyenMai,phuongThucThanhToan,tongTien,trangThai) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, px.getMaPhieuXuat());
            ps.setDate(2, Date.valueOf(px.getNgayLap()));
            ps.setString(3, px.getMaNhanVien());
            ps.setString(4, px.getMaKhachHang());
            if (px.getMaKhuyenMai() != null)
                ps.setString(5, px.getMaKhuyenMai());
            else
                ps.setNull(5, Types.VARCHAR);
            ps.setString(6, px.getPhuongThucThanhToan());
            ps.setDouble(7, px.getTongTien());
            ps.setString(8, px.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PhieuXuat px) {
        String sql = "UPDATE PhieuXuat SET ngayLap=?,maNhanVien=?,maKhachHang=?,maKhuyenMai=?,phuongThucThanhToan=?,tongTien=?,trangThai=? WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(px.getNgayLap()));
            ps.setString(2, px.getMaNhanVien());
            ps.setString(3, px.getMaKhachHang());
            if (px.getMaKhuyenMai() != null)
                ps.setString(4, px.getMaKhuyenMai());
            else
                ps.setNull(4, Types.VARCHAR);
            ps.setString(5, px.getPhuongThucThanhToan());
            ps.setDouble(6, px.getTongTien());
            ps.setString(7, px.getTrangThai());
            ps.setString(8, px.getMaPhieuXuat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String maPhieuXuat, String trangThai) {
        String sql = "UPDATE PhieuXuat SET trangThai=? WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maPhieuXuat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPhieuXuat) {
        String sql = "DELETE FROM PhieuXuat WHERE maPhieuXuat=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuXuat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaPhieuXuat() {
        String sql = "SELECT maPhieuXuat FROM PhieuXuat ORDER BY maPhieuXuat DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maPhieuXuat").replaceAll("[^0-9]", ""));
                return String.format("PX%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PX001";
    }
}
