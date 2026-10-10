package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.MauSac;

public class MauSacDAO {
    public ArrayList<MauSac> getAll() {
        ArrayList<MauSac> ds = new ArrayList<>();
        String sql = "SELECT * FROM MauSac ORDER BY maMauSac ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MauSac ms = new MauSac(
                        rs.getString("maMauSac"),
                        rs.getString("tenMauSac"));
                ds.add(ms);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(MauSac ms) {
        String sql = "INSERT INTO MauSac(maMauSac,tenMauSac) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ms.getMaMauSac());
            ps.setString(2, ms.getTenMauSac());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(MauSac ms) {
        String sql = "UPDATE MauSac SET tenMauSac=? WHERE maMauSac=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ms.getTenMauSac());
            ps.setString(2, ms.getMaMauSac());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maMauSac) {
        String sql = "DELETE FROM MauSac WHERE maMauSac=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMauSac);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
