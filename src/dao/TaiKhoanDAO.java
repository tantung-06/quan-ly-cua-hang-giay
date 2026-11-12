package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.TaiKhoan;

public class TaiKhoanDAO {
    public ArrayList<TaiKhoan> getAll() {
        ArrayList<TaiKhoan> ds = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan ORDER BY maTaiKhoan ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public TaiKhoan getById(String maTaiKhoan) {
        String sql = "SELECT * FROM TaiKhoan WHERE maTaiKhoan=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
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

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=? AND matKhau=? AND trangThai='HOAT_DONG'";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
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

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan(maTaiKhoan,tenDangNhap,matKhau,maNhomQuyen,trangThai) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getMaNhomQuyen());
            ps.setString(5, tk.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET tenDangNhap=?,matKhau=?,maNhomQuyen=?,trangThai=? WHERE maTaiKhoan=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getMaNhomQuyen());
            ps.setString(4, tk.getTrangThai());
            ps.setString(5, tk.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maTaiKhoan) {
        String sql = "DELETE FROM TaiKhoan WHERE maTaiKhoan=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<TaiKhoan> getByTrangThai(String trangThai) {
        ArrayList<TaiKhoan> ds = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE trangThai=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
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

    public boolean updateTrangThai(String maTaiKhoan, String trangThai) {
        String sql = "UPDATE TaiKhoan SET trangThai=? WHERE maTaiKhoan=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maTaiKhoan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaTaiKhoan() {
        String sql = "SELECT maTaiKhoan FROM TaiKhoan ORDER BY maTaiKhoan DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maTaiKhoan").replaceAll("[^0-9]", ""));
                return String.format("TK%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "TK001";
    }

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getString("maTaiKhoan"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("maNhomQuyen"),
                rs.getString("trangThai"));
    }
}