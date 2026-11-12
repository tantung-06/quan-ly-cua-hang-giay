package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import bus.DiemTichLuyBUS;
import config.DatabaseConnection;
import config.DatabaseConnection.ConnectionFactory;
import dto.ChiTietHoaDon;
import dto.HoaDon;

public class HoaDonDAO {
    private final ConnectionFactory connections;
    private final DiemTichLuyBUS loyalty;

    public HoaDonDAO() {
        this(DatabaseConnection::getConnection, new DiemTichLuyBUS());
    }

    public HoaDonDAO(ConnectionFactory connections, DiemTichLuyBUS loyalty) {
        this.connections = java.util.Objects.requireNonNull(connections);
        this.loyalty = java.util.Objects.requireNonNull(loyalty);
    }

    private static final String INSERT_SQL = "INSERT INTO HoaDon(maHoaDon,ngayLap,maNhanVien,maKhachHang,maKhuyenMai,phuongThucThanhToan,tongTien,trangThai,diemSuDung,diemDuocCong,diemDaXuLy) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_DETAIL_SQL = "INSERT INTO ChiTietHoaDon(maHoaDon,maSanPham,soLuong,donGia,thanhTien) VALUES(?,?,?,?,?)";

    public ArrayList<HoaDon> getAll() {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon ORDER BY ngayLap DESC";
        try (Connection conn = connections.open();
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

    public ArrayList<HoaDon> getByTrangThai(String trangThai) {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE trangThai=? ORDER BY ngayLap DESC";
        try (Connection conn = connections.open();
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

    public ArrayList<HoaDon> getByKhachHang(String maKhachHang) {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE maKhachHang=? ORDER BY ngayLap DESC";
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
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

    public ArrayList<HoaDon> getByNhanVien(String maNhanVien) {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE maNhanVien=? ORDER BY ngayLap DESC";
        try (Connection conn = connections.open();
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

    public HoaDon getById(String maHoaDon) {
        String sql = "SELECT * FROM HoaDon WHERE maHoaDon=?";
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
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

    public boolean insert(HoaDon sale) {
        if (!"CHO_XU_LY".equals(sale.getTrangThai()) || sale.isDiemDaXuLy() || sale.getDiemDuocCong() != 0) {
            return false;
        }
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            bindHoaDon(ps, sale);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertWithDetails(HoaDon sale, List<ChiTietHoaDon> details) {
        if (!"CHO_XU_LY".equals(sale.getTrangThai()) || sale.isDiemDaXuLy()
                || sale.getDiemDuocCong() != 0 || sale.getDiemSuDung() < 0 || details == null || details.isEmpty()) {
            return false;
        }
        try (Connection conn = connections.open()) {
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement header = conn.prepareStatement(INSERT_SQL);
                        PreparedStatement lines = conn.prepareStatement(INSERT_DETAIL_SQL)) {
                    bindHoaDon(header, sale);
                    if (header.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                    for (ChiTietHoaDon detail : details) {
                        lines.setString(1, detail.getMaHoaDon());
                        lines.setString(2, detail.getMaSanPham());
                        lines.setInt(3, detail.getSoLuong());
                        lines.setDouble(4, detail.getDonGia());
                        lines.setDouble(5, detail.getThanhTien());
                        lines.addBatch();
                    }
                    for (int count : lines.executeBatch()) {
                        if (count == java.sql.Statement.EXECUTE_FAILED || count == 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void bindHoaDon(PreparedStatement ps, HoaDon sale) throws SQLException {
        ps.setString(1, sale.getMaHoaDon());
        ps.setDate(2, Date.valueOf(sale.getNgayLap()));
        ps.setString(3, sale.getMaNhanVien());
        ps.setString(4, sale.getMaKhachHang());
        ps.setString(5, sale.getMaKhuyenMai());
        ps.setString(6, sale.getPhuongThucThanhToan());
        ps.setDouble(7, sale.getTongTien());
        ps.setString(8, sale.getTrangThai());
        ps.setInt(9, sale.getDiemSuDung());
        ps.setInt(10, sale.getDiemDuocCong());
        ps.setBoolean(11, sale.isDiemDaXuLy());
    }

    public boolean update(HoaDon sale) {
        if (!"CHO_XU_LY".equals(sale.getTrangThai()) || sale.getDiemSuDung() != 0
                || sale.getDiemDuocCong() != 0 || sale.isDiemDaXuLy()) {
            return false;
        }
        String sql = "UPDATE HoaDon SET ngayLap=?,maNhanVien=?,maKhachHang=?,maKhuyenMai=?,phuongThucThanhToan=?,tongTien=?,trangThai=? WHERE maHoaDon=? AND trangThai='CHO_XU_LY' AND diemSuDung=0 AND diemDaXuLy=FALSE";
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(sale.getNgayLap()));
            ps.setString(2, sale.getMaNhanVien());
            ps.setString(3, sale.getMaKhachHang());
            if (sale.getMaKhuyenMai() != null)
                ps.setString(4, sale.getMaKhuyenMai());
            else
                ps.setNull(4, Types.VARCHAR);
            ps.setString(5, sale.getPhuongThucThanhToan());
            ps.setDouble(6, sale.getTongTien());
            ps.setString(7, sale.getTrangThai());
            ps.setString(8, sale.getMaHoaDon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String id, String state) {
        if ("DA_XUAT".equals(state)) {
            return xuatKho(id);
        }
        return "HUY".equals(state) && huyPhieu(id);
    }

    public boolean xuatKho(String id) {
        return changeState(id, false);
    }

    public boolean huyPhieu(String id) {
        return changeState(id, true);
    }

    private boolean changeState(String id, boolean cancel) {
        try (Connection connection = connections.open()) {
            try {
                connection.setAutoCommit(false);
                HoaDon sale;
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT * FROM HoaDon WHERE maHoaDon=? FOR UPDATE")) {
                    lock.setString(1, id);
                    try (ResultSet rows = lock.executeQuery()) {
                        sale = rows.next() ? mapRow(rows) : null;
                    }
                }
                if (sale == null || !"CHO_XU_LY".equals(sale.getTrangThai()) || sale.isDiemDaXuLy()) {
                    connection.rollback();
                    return false;
                }
                if (!cancel) {
                    // Lock order is always receipt -> customer -> products sorted by ID.
                    validatePromotionUsage(connection, sale);
                    loyalty.thanhToan(connection, sale);
                    updateStock(connection, id);
                }
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE HoaDon SET trangThai=?,diemDuocCong=?,diemDaXuLy=? "
                                + "WHERE maHoaDon=? AND trangThai=?")) {
                    update.setString(1, cancel ? "HUY" : "DA_XUAT");
                    update.setInt(2, sale.getDiemDuocCong());
                    update.setBoolean(3, sale.isDiemDaXuLy());
                    update.setString(4, id);
                    update.setString(5, sale.getTrangThai());
                    if (update.executeUpdate() != 1) {
                        throw new SQLException("Trạng thái phiếu đã thay đổi.");
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                if (ex instanceof IllegalArgumentException) {
                    throw (IllegalArgumentException) ex;
                }
                throw new IllegalStateException("Không thể cập nhật phiếu, tồn kho và điểm. Dữ liệu đã được hoàn tác.",
                        ex);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể kết nối cơ sở dữ liệu để xử lý phiếu.", ex);
        }
    }

    private void validatePromotionUsage(Connection connection, HoaDon sale) throws SQLException {
        if (sale.getMaKhuyenMai() == null) {
            return;
        }
        if (sale.getMaKhachHang() == null || sale.getMaKhachHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn khách hàng trước khi áp dụng khuyến mãi.");
        }
        if (new KhachHangDAO().getForUpdate(connection, sale.getMaKhachHang()) == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng để áp dụng khuyến mãi.");
        }
        if (new KhuyenMaiDAO().getMaDaSuDung(connection, sale.getMaKhachHang())
                .contains(sale.getMaKhuyenMai().trim())) {
            throw new IllegalArgumentException("Khách hàng đã sử dụng mã khuyến mãi này trên phiếu khác.");
        }
    }

    private void updateStock(Connection connection, String id) throws SQLException {
        ArrayList<String> productIds = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        try (PreparedStatement details = connection.prepareStatement(
                "SELECT maSanPham,soLuong FROM ChiTietHoaDon WHERE maHoaDon=? ORDER BY maSanPham")) {
            details.setString(1, id);
            try (ResultSet rows = details.executeQuery()) {
                while (rows.next()) {
                    productIds.add(rows.getString("maSanPham"));
                    quantities.add(rows.getInt("soLuong"));
                }
            }
        }
        if (productIds.isEmpty()) {
            throw new IllegalArgumentException("Phiếu không có sản phẩm để xử lý.");
        }
        String sql = "UPDATE SanPham SET soLuongTon=soLuongTon-? WHERE maSanPham=? AND soLuongTon>=?";
        try (PreparedStatement stock = connection.prepareStatement(sql)) {
            for (int i = 0; i < productIds.size(); i++) {
                stock.setInt(1, quantities.get(i));
                stock.setString(2, productIds.get(i));
                stock.setInt(3, quantities.get(i));
                if (stock.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Không đủ tồn kho hoặc sản phẩm đã thay đổi.");
                }
            }
        }
    }

    public boolean delete(String maHoaDon) {
        String sql = "DELETE FROM HoaDon WHERE maHoaDon=? AND trangThai<>'DA_XUAT' AND diemDaXuLy=FALSE";
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateMaHoaDon() {
        String sql = "SELECT maHoaDon FROM HoaDon ORDER BY maHoaDon DESC LIMIT 1";
        try (Connection conn = connections.open();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("maHoaDon").replaceAll("[^0-9]", ""));
                return String.format("HD%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "HD001";
    }

    private HoaDon mapRow(ResultSet rs) throws SQLException {
        HoaDon sale = new HoaDon(
                rs.getString("maHoaDon"),
                rs.getDate("ngayLap").toLocalDate(),
                rs.getString("maNhanVien"),
                rs.getString("maKhachHang"),
                rs.getString("maKhuyenMai"),
                rs.getString("phuongThucThanhToan"),
                rs.getDouble("tongTien"),
                rs.getString("trangThai"));
        sale.setDiemSuDung(rs.getInt("diemSuDung"));
        sale.setDiemDuocCong(rs.getInt("diemDuocCong"));
        sale.setDiemDaXuLy(rs.getBoolean("diemDaXuLy"));
        return sale;
    }
}