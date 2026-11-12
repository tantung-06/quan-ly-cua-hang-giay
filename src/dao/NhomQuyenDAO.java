package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.NhomQuyen;

public class NhomQuyenDAO {
    public ArrayList<NhomQuyen> getAll() {
        ArrayList<NhomQuyen> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhomQuyen ORDER BY maNhomQuyen ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public NhomQuyen getByID(String maNhomQuyen) {
        String sql = "SELECT * FROM NhomQuyen WHERE maNhomQuyen=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhomQuyen);
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

    public boolean insert(NhomQuyen nq) {
        String sql = "INSERT INTO NhomQuyen(maNhomQuyen,tenNhomQuyen) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nq.getMaNhomQuyen());
            ps.setString(2, nq.getTenNhomQuyen());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhomQuyen nq) {
        String sql = "UPDATE NhomQuyen SET tenNhomQuyen=? WHERE maNhomQuyen=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nq.getTenNhomQuyen());
            ps.setString(2, nq.getMaNhomQuyen());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maNhomQuyen) {
        String sql = "DELETE FROM NhomQuyen WHERE maNhomQuyen=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhomQuyen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaNhomQuyen() {
        String sql = "SELECT maNhomQuyen FROM NhomQuyen ORDER BY maNhomQuyen DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maNhomQuyen").replaceAll("[^0-9]", ""));
                return String.format("NQ%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NQ001";
    }

    private NhomQuyen mapRow(ResultSet rs) throws SQLException {
        return new NhomQuyen(
                rs.getString("maNhomQuyen"),
                rs.getString("tenNhomQuyen"));
    }
}