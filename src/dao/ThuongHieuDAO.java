package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.ThuongHieu;

public class ThuongHieuDAO {
    public ArrayList<ThuongHieu> getAll() {
        ArrayList<ThuongHieu> ds = new ArrayList<>();
        String sql = "SELECT * FROM ThuongHieu ORDER BY maThuongHieu ASC";
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

    public boolean insert(ThuongHieu th) {
        String sql = "INSERT INTO ThuongHieu(maThuongHieu,tenThuongHieu) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, th.getMaThuongHieu());
            ps.setString(2, th.getTenThuongHieu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ThuongHieu th) {
        String sql = "UPDATE ThuongHieu SET tenThuongHieu=? WHERE maThuongHieu=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, th.getTenThuongHieu());
            ps.setString(2, th.getMaThuongHieu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maThuongHieu) {
        String sql = "DELETE FROM ThuongHieu WHERE maThuongHieu=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThuongHieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaThuongHieu() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(maThuongHieu, 3) AS UNSIGNED)), 0) + 1 AS nextCode "
                + "FROM ThuongHieu WHERE maThuongHieu REGEXP '^TH[0-9]+$'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return String.format("TH%03d", rs.getInt("nextCode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ThuongHieu mapRow(ResultSet rs) throws SQLException {
        return new ThuongHieu(
                rs.getString("maThuongHieu"),
                rs.getString("tenThuongHieu"));
    }
}