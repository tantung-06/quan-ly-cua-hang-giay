package bus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import dao.KhuyenMaiDAO;
import dto.KhuyenMai;

public class KhuyenMaiBUS {

    private final KhuyenMaiDAO khuyenMaiDAO;

    public KhuyenMaiBUS() {
        this(new KhuyenMaiDAO());
    }

    public KhuyenMaiBUS(KhuyenMaiDAO khuyenMaiDAO) {
        this.khuyenMaiDAO = Objects.requireNonNull(khuyenMaiDAO);
    }

    public ArrayList<KhuyenMai> getAll() {
        return khuyenMaiDAO.getAll();
    }

    public ArrayList<KhuyenMai> getApDung() {
        return khuyenMaiDAO.getApDung();
    }

    public Set<String> getMaDaSuDung(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return khuyenMaiDAO.getMaDaSuDung(customerId.trim());
    }

    public void kiemTraLuotSuDung(String customerId, String promotionId) {
        if (promotionId == null || promotionId.trim().isEmpty()) {
            return;
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn khách hàng trước khi áp dụng khuyến mãi.");
        }
        if (getMaDaSuDung(customerId).stream().anyMatch(code -> code.equalsIgnoreCase(promotionId.trim()))) {
            throw new IllegalArgumentException("Khách hàng đã sử dụng mã khuyến mãi này. Vui lòng chọn mã khác.");
        }
    }

    public ArrayList<KhuyenMai> getHieuLuc(LocalDate ngayLap) {
        Objects.requireNonNull(ngayLap);
        ArrayList<KhuyenMai> result = new ArrayList<>();
        for (KhuyenMai promotion : getApDung()) {
            if (isDuLieuHopLe(promotion) && "AP_DUNG".equals(promotion.getTrangThai())
                    && !ngayLap.isBefore(promotion.getNgayBatDau())
                    && !ngayLap.isAfter(promotion.getNgayKetThuc())) {
                result.add(promotion);
            }
        }
        return result;
    }

    public double tinhTienGiam(KhuyenMai promotion, double tienHang, LocalDate ngayLap) {
        if (!Double.isFinite(tienHang) || tienHang < 0 || ngayLap == null) {
            throw new IllegalArgumentException("Tiền hàng hoặc ngày lập phiếu không hợp lệ.");
        }
        if (promotion == null) {
            return 0;
        }
        if (!isDuLieuHopLe(promotion) || !"AP_DUNG".equals(promotion.getTrangThai())) {
            throw new IllegalArgumentException("Khuyến mãi không còn áp dụng hoặc dữ liệu không hợp lệ.");
        }
        if (ngayLap.isBefore(promotion.getNgayBatDau()) || ngayLap.isAfter(promotion.getNgayKetThuc())) {
            throw new IllegalArgumentException("Khuyến mãi nằm ngoài thời gian áp dụng.");
        }
        if (tienHang < promotion.getDonHangToiThieu()) {
            throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu của khuyến mãi.");
        }
        BigDecimal subtotal = BigDecimal.valueOf(tienHang).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.valueOf(promotion.getMucGiam());
        if ("GIAM_TI_LE".equals(promotion.getLoaiKhuyenMai())) {
            discount = subtotal.multiply(discount).movePointLeft(2);
        }
        return discount.min(subtotal).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double tinhTongThanhToan(KhuyenMai promotion, double tienHang, LocalDate ngayLap) {
        double discount = tinhTienGiam(promotion, tienHang, ngayLap);
        return BigDecimal.valueOf(tienHang).subtract(BigDecimal.valueOf(discount))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public ArrayList<KhuyenMai> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return khuyenMaiDAO.getByTrangThai(trangThai.trim());
    }

    public KhuyenMai getById(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            return null;
        }
        return khuyenMaiDAO.getById(maKhuyenMai.trim());
    }

    public boolean insert(KhuyenMai km) {
        if (!isDuLieuHopLe(km)) {
            return false;
        }
        chuanHoaDuLieu(km);
        if (!isTrangThaiHopLe(km.getTrangThai())) {
            return false;
        }
        return khuyenMaiDAO.insert(km);
    }

    public boolean update(KhuyenMai km) {
        if (!isDuLieuHopLe(km)) {
            return false;
        }
        chuanHoaDuLieu(km);
        if (!isTrangThaiHopLe(km.getTrangThai())) {
            return false;
        }
        return khuyenMaiDAO.update(km);
    }

    private boolean isDuLieuHopLe(KhuyenMai km) {
        if (km == null) {
            return false;
        }
        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().trim().isEmpty()) {
            return false;
        }
        if (km.getTenKhuyenMai() == null || km.getTenKhuyenMai().trim().isEmpty()) {
            return false;
        }
        if (km.getLoaiKhuyenMai() == null || km.getLoaiKhuyenMai().trim().isEmpty()) {
            return false;
        }
        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null) {
            return false;
        }
        if (km.getNgayKetThuc().isBefore(km.getNgayBatDau())) {
            return false;
        }
        if (!Double.isFinite(km.getDonHangToiThieu()) || km.getDonHangToiThieu() < 0
                || !Double.isFinite(km.getMucGiam()) || km.getMucGiam() <= 0) {
            return false;
        }
        if (km.getTrangThai() == null || km.getTrangThai().trim().isEmpty()) {
            return false;
        }
        String loai = km.getLoaiKhuyenMai().trim();
        if (!"GIAM_TI_LE".equals(loai) && !"GIAM_TIEN".equals(loai)) {
            return false;
        }
        if ("GIAM_TI_LE".equals(loai) && km.getMucGiam() > 100) {
            return false;
        }

        return true;
    }

    private void chuanHoaDuLieu(KhuyenMai km) {
        km.setMaKhuyenMai(km.getMaKhuyenMai().trim());
        km.setTenKhuyenMai(km.getTenKhuyenMai().trim());
        km.setLoaiKhuyenMai(km.getLoaiKhuyenMai().trim());
        km.setTrangThai(km.getTrangThai().trim());
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        if (trangThai == null) {
            return false;
        }
        String tt = trangThai.trim();
        return "AP_DUNG".equals(tt) || "HET_HAN".equals(tt);
    }

    public boolean updateTrangThai(
            String maKhuyenMai, String trangThai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        return khuyenMaiDAO.updateTrangThai(maKhuyenMai.trim(), trangThai.trim());
    }

    public boolean delete(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            return false;
        }
        return khuyenMaiDAO.delete(maKhuyenMai.trim());
    }

    public String generateMaKhuyenMai() {
        return khuyenMaiDAO.generateMaKhuyenMai();
    }
}