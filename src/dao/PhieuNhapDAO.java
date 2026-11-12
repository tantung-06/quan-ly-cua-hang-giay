package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                ds.add(mapRow(rs));
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

    public ArrayList<PhieuNhap> getByNhanVien(String maNhanVien) {
        ArrayList<PhieuNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE maNhanVien=? ORDER BY ngayNhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
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

    public PhieuNhap getById(String maPhieuNhap) {
        String sql = "SELECT * FROM PhieuNhap WHERE maPhieuNhap=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
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
        String sql = "UPDATE PhieuNhap SET trangThai=? WHERE maPhieuNhap=? AND trangThai='CHO_XU_LY'";
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

    public boolean nhapKho(String maPhieuNhap) {
        String lockSql = "SELECT trangThai FROM PhieuNhap WHERE maPhieuNhap=? FOR UPDATE";
        String detailsSql = "SELECT maSanPham, soLuong FROM ChiTietPhieuNhap WHERE maPhieuNhap=? ORDER BY maSanPham";
        String stockSql = "UPDATE SanPham SET soLuongTon=soLuongTon+? WHERE maSanPham=?";
        String completeSql = "UPDATE PhieuNhap SET trangThai='DA_NHAP' WHERE maPhieuNhap=? AND trangThai='CHO_XU_LY'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement lock = conn.prepareStatement(lockSql)) {
                    lock.setString(1, maPhieuNhap);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next() || !"CHO_XU_LY".equals(rs.getString("trangThai"))) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                ArrayList<String> productIds = new ArrayList<>();
                ArrayList<Integer> quantities = new ArrayList<>();
                try (PreparedStatement details = conn.prepareStatement(detailsSql)) {
                    details.setString(1, maPhieuNhap);
                    try (ResultSet rs = details.executeQuery()) {
                        while (rs.next()) {
                            productIds.add(rs.getString("maSanPham"));
                            quantities.add(rs.getInt("soLuong"));
                        }
                    }
                }
                if (productIds.isEmpty()) {
                    conn.rollback();
                    return false;
                }
                try (PreparedStatement stock = conn.prepareStatement(stockSql)) {
                    for (int i = 0; i < productIds.size(); i++) {
                        stock.setInt(1, quantities.get(i));
                        stock.setString(2, productIds.get(i));
                        if (stock.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement complete = conn.prepareStatement(completeSql)) {
                    complete.setString(1, maPhieuNhap);
                    if (complete.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                e.printStackTrace();
                return false;
            } finally {
                try {
                    conn.setAutoCommit(previousAutoCommit);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    private PhieuNhap mapRow(ResultSet rs) throws SQLException {
        return new PhieuNhap(
                rs.getString("maPhieuNhap"),
                rs.getDate("ngayNhap").toLocalDate(),
                rs.getString("maNhanVien"),
                rs.getString("maNCC"),
                rs.getDouble("tongTien"),
                rs.getString("trangThai"));
    }
}