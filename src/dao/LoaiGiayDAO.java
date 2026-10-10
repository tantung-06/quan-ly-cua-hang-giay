package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.LoaiGiay;

public class LoaiGiayDAO {
    public ArrayList<LoaiGiay> getAll() {
        ArrayList<LoaiGiay> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiGiay ORDER BY maLoaiGiay ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiGiay lg = new LoaiGiay(
                        rs.getString("maLoaiGiay"),
                        rs.getString("tenLoaiGiay"));
                ds.add(lg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(LoaiGiay lg) {
        String sql = "INSERT INTO LoaiGiay(maLoaiGiay,tenLoaiGiay) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lg.getMaLoaiGiay());
            ps.setString(2, lg.getTenLoaiGiay());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(LoaiGiay lg) {
        String sql = "UPDATE LoaiGiay SET tenLoaiGiay=? WHERE maLoaiGiay=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lg.getTenLoaiGiay());
            ps.setString(2, lg.getMaLoaiGiay());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maLoaiGiay) {
        String sql = "DELETE FROM LoaiGiay WHERE maLoaiGiay=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLoaiGiay);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
