package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DatabaseConnection;
import dto.KichThuoc;

public class KichThuocDAO {
    public ArrayList<KichThuoc> getAll() {
        ArrayList<KichThuoc> ds = new ArrayList<>();
        String sql = "SELECT * FROM KichThuoc ORDER BY maKichThuoc ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KichThuoc kt = new KichThuoc(
                        rs.getString("maKichThuoc"),
                        rs.getString("tenKichThuoc"));
                ds.add(kt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(KichThuoc kt) {
        String sql = "INSERT INTO KichThuoc(maKichThuoc,tenKichThuoc) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kt.getMaKichThuoc());
            ps.setString(2, kt.getTenKichThuoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KichThuoc kt) {
        String sql = "UPDATE KichThuoc SET tenKichThuoc=? WHERE maKichThuoc=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kt.getTenKichThuoc());
            ps.setString(2, kt.getMaKichThuoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maKichThuoc) {
        String sql = "DELETE FROM KichThuoc WHERE maKichThuoc=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKichThuoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
