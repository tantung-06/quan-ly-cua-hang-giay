package bus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import dao.KhachHangDAO;
import dao.LichSuDiemDAO;
import dto.KhachHang;
import dto.LichSuDiem;
import dto.HoaDon;
import dto.SoDuDiem;

public class DiemTichLuyBUS {
    public static final int VND_MOI_DIEM_CONG = 10000;
    public static final int VND_MOI_DIEM_DUNG = 1000;
    private final KhachHangDAO customers;
    private final LichSuDiemDAO history;

    public DiemTichLuyBUS() {
        this(new KhachHangDAO(), new LichSuDiemDAO());
    }

    public DiemTichLuyBUS(KhachHangDAO customers, LichSuDiemDAO history) {
        this.customers = Objects.requireNonNull(customers);
        this.history = Objects.requireNonNull(history);
    }

    public static int tinhDiemCong(double thucTra) {
        BigDecimal amount = money(thucTra);
        try {
            return amount.divideToIntegralValue(BigDecimal.valueOf(VND_MOI_DIEM_CONG)).intValueExact();
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Số điểm vượt quá giới hạn.");
        }
    }

    public static double tinhThucTra(double sauKhuyenMai, int diemDung, int diemHienCo) {
        BigDecimal amount = money(sauKhuyenMai);
        if (diemDung < 0 || diemHienCo < 0 || diemDung > diemHienCo) {
            throw new IllegalArgumentException("Số điểm sử dụng vượt quá số điểm hiện có.");
        }
        BigDecimal reduction = BigDecimal.valueOf(diemDung).multiply(BigDecimal.valueOf(VND_MOI_DIEM_DUNG));
        if (reduction.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Tiền giảm từ điểm vượt quá tiền cần thanh toán.");
        }
        return amount.subtract(reduction).doubleValue();
    }

    public static SoDuDiem bienDong(SoDuDiem balance, long change) {
        long net = Math.addExact((long) balance.getDiemHienCo(), change);
        if (net > Integer.MAX_VALUE || net < 0) {
            throw new IllegalArgumentException("Số dư điểm không được âm hoặc vượt quá giới hạn.");
        }
        return new SoDuDiem((int) net);
    }

    public ArrayList<LichSuDiem> getLichSu(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return history.getByKhachHang(customerId.trim());
    }

    public void thanhToan(Connection connection, HoaDon sale) throws SQLException {
        if (sale.isDiemDaXuLy()) {
            throw new IllegalArgumentException("Điểm của phiếu xuất đã được xử lý.");
        }
        if (sale.getMaKhachHang() == null) {
            if (sale.getDiemSuDung() != 0) {
                throw new IllegalArgumentException("Khách lẻ không thể sử dụng điểm.");
            }
            sale.setDiemDuocCong(0);
            sale.setDiemDaXuLy(true);
            return;
        }
        KhachHang customer = customers.getForUpdate(connection, sale.getMaKhachHang());
        if (customer == null || !"HOAT_DONG".equals(customer.getTrangThai())) {
            throw new IllegalArgumentException("Khách hàng không còn hoạt động.");
        }
        if (sale.getDiemSuDung() < 0 || sale.getDiemSuDung() > customer.getDiemTichLuy()) {
            throw new IllegalArgumentException("Điểm khách hàng đã thay đổi, không đủ điểm để thanh toán phiếu xuất.");
        }
        SoDuDiem balance = new SoDuDiem(customer.getDiemTichLuy());
        balance = apply(connection, sale, balance, "TRU", -sale.getDiemSuDung());
        int earned = tinhDiemCong(sale.getTongTien());
        balance = apply(connection, sale, balance, "CONG", earned);
        customers.updateDiem(connection, customer.getMaKhachHang(), balance);
        sale.setDiemDuocCong(earned);
        sale.setDiemDaXuLy(true);
    }

    private SoDuDiem apply(Connection connection, HoaDon sale, SoDuDiem balance,
            String type, int change) throws SQLException {
        if (change == 0) {
            return balance;
        }
        SoDuDiem next = bienDong(balance, change);
        history.insert(connection, new LichSuDiem(0, sale.getMaKhachHang(), sale.getMaHoaDon(), type,
                Math.abs(change), next.getDiemHienCo(), null));
        return next;
    }

    private static BigDecimal money(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ.");
        }
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
}