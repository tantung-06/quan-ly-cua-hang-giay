package bus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import dao.ChiTietHoaDonDAO;
import dao.KhachHangDAO;
import dao.HoaDonDAO;
import dto.ChiTietHoaDon;
import dto.KhachHang;
import dto.KhuyenMai;
import dto.HoaDon;

public class HoaDonBUS {

    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final KhuyenMaiBUS khuyenMaiBUS;
    private final KhachHangDAO customers;

    public HoaDonBUS() {
        this(new HoaDonDAO(), new ChiTietHoaDonDAO(), new KhuyenMaiBUS());
    }

    public HoaDonBUS(HoaDonDAO HoaDonDAO, ChiTietHoaDonDAO chiTietHoaDonDAO,
            KhuyenMaiBUS khuyenMaiBUS) {
        this(HoaDonDAO, chiTietHoaDonDAO, khuyenMaiBUS, new KhachHangDAO());
    }

    public HoaDonBUS(HoaDonDAO HoaDonDAO, ChiTietHoaDonDAO chiTietHoaDonDAO,
            KhuyenMaiBUS khuyenMaiBUS, KhachHangDAO customers) {
        this.hoaDonDAO = Objects.requireNonNull(HoaDonDAO);
        this.chiTietHoaDonDAO = Objects.requireNonNull(chiTietHoaDonDAO);
        this.khuyenMaiBUS = Objects.requireNonNull(khuyenMaiBUS);
        this.customers = Objects.requireNonNull(customers);
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "CHO_XU_LY".equals(trangThai) || "DA_XUAT".equals(trangThai) || "HUY".equals(trangThai);
    }

    private String validateHoaDon(HoaDon hd) {
        if (hd == null) {
            return "Phiếu xuất không được để trống!";
        }
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            return "Mã phiếu xuất không được để trống!";
        }
        if (hd.getNgayLap() == null) {
            return "Ngày lập phiếu xuất không được để trống!";
        }
        if (hd.getNgayLap().isAfter(LocalDate.now())) {
            return "Ngày lập phiếu xuất không được lớn hơn ngày hiện tại!";
        }
        if (hd.getMaNhanVien() == null || hd.getMaNhanVien().trim().isEmpty()) {
            return "Vui lòng chọn nhân viên lập phiếu xuất!";
        }
        if (hd.getPhuongThucThanhToan() == null || hd.getPhuongThucThanhToan().trim().isEmpty()) {
            return "Phương thức thanh toán không được để trống!";
        }
        if (!Double.isFinite(hd.getTongTien()) || hd.getTongTien() < 0) {
            return "Tổng tiền không hợp lệ!";
        }
        if (!isTrangThaiHopLe(hd.getTrangThai())) {
            return "Trạng thái phiếu xuất không hợp lệ!";
        }
        if (hd.getDiemSuDung() < 0 || hd.getDiemDuocCong() < 0
                || (hd.getMaKhachHang() == null && hd.getDiemSuDung() != 0)) {
            return "Điểm sử dụng không hợp lệ hoặc chưa chọn khách hàng.";
        }
        return null;
    }

    public ArrayList<HoaDon> getAll() {
        return hoaDonDAO.getAll();
    }

    public HoaDon getById(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return null;
        }
        return hoaDonDAO.getById(maHoaDon.trim());
    }

    public ArrayList<HoaDon> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return hoaDonDAO.getByTrangThai(trangThai);
    }

    public ArrayList<HoaDon> getByKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return hoaDonDAO.getByKhachHang(maKhachHang.trim());
    }

    public ArrayList<HoaDon> getByNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return hoaDonDAO.getByNhanVien(maNhanVien.trim());
    }

    public boolean insert(HoaDon hd) {
        String loi = validateHoaDon(hd);
        if (loi != null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai()) || hd.isDiemDaXuLy()
                || hd.getDiemDuocCong() != 0 || hd.getDiemSuDung() != 0) {
            return false;
        }
        if (hoaDonDAO.getById(hd.getMaHoaDon().trim()) != null) {
            return false;
        }
        return hoaDonDAO.insert(hd);
    }

    public boolean lapPhieu(HoaDon hd, ArrayList<ChiTietHoaDon> details) {
        if (validateHoaDon(hd) != null || !"CHO_XU_LY".equals(hd.getTrangThai())
                || hd.isDiemDaXuLy() || hd.getDiemDuocCong() != 0
                || details == null || details.isEmpty()) {
            return false;
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        Set<String> productIds = new HashSet<>();
        for (ChiTietHoaDon detail : details) {
            if (validateChiTiet(detail) != null
                    || !hd.getMaHoaDon().equals(detail.getMaHoaDon())
                    || !productIds.add(detail.getMaSanPham())) {
                return false;
            }
            subtotal = subtotal.add(BigDecimal.valueOf(detail.getDonGia())
                    .multiply(BigDecimal.valueOf(detail.getSoLuong())));
        }
        KhuyenMai promotion = null;
        if (hd.getMaKhuyenMai() != null && !hd.getMaKhuyenMai().trim().isEmpty()) {
            khuyenMaiBUS.kiemTraLuotSuDung(hd.getMaKhachHang(), hd.getMaKhuyenMai());
            promotion = khuyenMaiBUS.getById(hd.getMaKhuyenMai());
            if (promotion == null) {
                throw new IllegalArgumentException("Khuyến mãi đã chọn không còn tồn tại. Vui lòng chọn lại.");
            }
            hd.setMaKhuyenMai(promotion.getMaKhuyenMai());
        } else {
            hd.setMaKhuyenMai(null);
        }
        double payable = khuyenMaiBUS.tinhTongThanhToan(promotion, subtotal.doubleValue(), hd.getNgayLap());
        int availablePoints = 0;
        if (hd.getMaKhachHang() != null) {
            KhachHang customer = customers.getById(hd.getMaKhachHang());
            if (customer == null || !"HOAT_DONG".equals(customer.getTrangThai())) {
                throw new IllegalArgumentException("Khách hàng không tồn tại hoặc đã ngừng hoạt động.");
            }
            availablePoints = customer.getDiemTichLuy();
            hd.setMaKhachHang(customer.getMaKhachHang());
        }
        payable = DiemTichLuyBUS.tinhThucTra(payable, hd.getDiemSuDung(), availablePoints);
        if (Math.abs(payable - hd.getTongTien()) > 0.005) {
            throw new IllegalArgumentException(
                    "Tổng thanh toán đã thay đổi. Vui lòng kiểm tra khuyến mãi và điểm sử dụng.");
        }
        hd.setTongTien(payable);
        return hoaDonDAO.insertWithDetails(hd, details);
    }

    public boolean update(HoaDon hd) {
        String loi = validateHoaDon(hd);
        if (loi != null) {
            return false;
        }
        if (hoaDonDAO.getById(hd.getMaHoaDon().trim()) == null) {
            return false;
        }
        return hoaDonDAO.update(hd);
    }

    public boolean updateTrangThai(String maHoaDon, String trangThai) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }
        if ("DA_XUAT".equals(trangThai)) {
            return hoanTatXuat(maHoaDon);
        }
        if ("HUY".equals(trangThai)) {
            return huyPhieu(maHoaDon);
        }
        return false;
    }

    public boolean huyPhieu(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }
        return hoaDonDAO.huyPhieu(maHoaDon.trim());
    }

    public boolean hoanTatXuat(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }
        return hoaDonDAO.xuatKho(maHoaDon.trim());
    }

    public boolean delete(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }
        String ma = maHoaDon.trim();
        HoaDon hd = hoaDonDAO.getById(ma);
        if (hd == null) {
            return false;
        }
        if ("DA_XUAT".equals(hd.getTrangThai()) || hd.isDiemDaXuLy()) {
            return false;
        }
        if (!chiTietHoaDonDAO.deleteByHoaDon(ma)) {
            return false;
        }
        return hoaDonDAO.delete(ma);
    }

    public String generateMaHoaDon() {
        return hoaDonDAO.generateMaHoaDon();
    }

    public ArrayList<ChiTietHoaDon> getChiTietByHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return chiTietHoaDonDAO.getByHoaDon(
                maHoaDon.trim());
    }

    public ChiTietHoaDon getChiTietById(String maHoaDon, String maSanPham) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        return chiTietHoaDonDAO.getById(maHoaDon.trim(), maSanPham.trim());
    }

    private String validateChiTiet(ChiTietHoaDon ct) {
        if (ct == null) {
            return "Chi tiết phiếu xuất không được để trống!";
        }
        if (ct.getMaHoaDon() == null || ct.getMaHoaDon().trim().isEmpty()) {
            return "Mã phiếu xuất không được để trống!";
        }
        if (ct.getMaSanPham() == null || ct.getMaSanPham().trim().isEmpty()) {
            return "Vui lòng chọn sản phẩm!";
        }
        if (ct.getSoLuong() <= 0) {
            return "Số lượng sản phẩm phải lớn hơn 0!";
        }
        if (!Double.isFinite(ct.getDonGia()) || ct.getDonGia() < 0) {
            return "Đơn giá không hợp lệ!";
        }
        if (!Double.isFinite(ct.getThanhTien()) || ct.getThanhTien() < 0) {
            return "Thành tiền không hợp lệ!";
        }
        double thanhTienDuKien = (double) ct.getSoLuong() * ct.getDonGia();
        if (Math.abs(ct.getThanhTien() - thanhTienDuKien) > 0.01) {
            return "Thành tiền phải bằng số lượng nhân đơn giá!";
        }
        return null;
    }

    public boolean insertChiTiet(ChiTietHoaDon ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        String maHoaDon = ct.getMaHoaDon().trim();
        HoaDon hd = hoaDonDAO.getById(maHoaDon);
        if (hd == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai())) {
            return false;
        }
        if (chiTietHoaDonDAO.getById(maHoaDon, ct.getMaSanPham().trim()) != null) {
            return false;
        }
        return chiTietHoaDonDAO.insert(ct);
    }

    public boolean insertBatchChiTiet(ArrayList<ChiTietHoaDon> ds) {
        if (ds == null || ds.isEmpty()) {
            return false;
        }
        ChiTietHoaDon dauTien = ds.get(0);
        if (dauTien == null || dauTien.getMaHoaDon() == null || dauTien.getMaHoaDon().trim().isEmpty()) {
            return false;
        }
        String maHoaDon = dauTien.getMaHoaDon().trim();
        HoaDon hd = hoaDonDAO.getById(maHoaDon);
        if (hd == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai())) {
            return false;
        }
        Set<String> maSanPhamDaCo = new HashSet<>();
        for (ChiTietHoaDon ct : ds) {
            String loi = validateChiTiet(ct);
            if (loi != null) {
                return false;
            }
            if (!maHoaDon.equals(ct.getMaHoaDon().trim())) {
                return false;
            }
            String maSanPham = ct.getMaSanPham().trim();
            if (!maSanPhamDaCo.add(maSanPham)) {
                return false;
            }
            if (chiTietHoaDonDAO.getById(maHoaDon, maSanPham) != null) {
                return false;
            }
        }
        return chiTietHoaDonDAO.insertBatch(ds);
    }

    public boolean updateChiTiet(ChiTietHoaDon ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        String maHoaDon = ct.getMaHoaDon().trim();
        String maSanPham = ct.getMaSanPham().trim();
        HoaDon hd = hoaDonDAO.getById(maHoaDon);
        if (hd == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai())) {
            return false;
        }
        if (chiTietHoaDonDAO.getById(maHoaDon, maSanPham) == null) {
            return false;
        }
        return chiTietHoaDonDAO.update(ct);
    }

    public boolean deleteChiTiet(String maHoaDon, String maSanPham) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return false;
        }
        String maPhieu = maHoaDon.trim();
        String maSanPhamChuan = maSanPham.trim();
        HoaDon hd = hoaDonDAO.getById(maPhieu);
        if (hd == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai())) {
            return false;
        }
        if (chiTietHoaDonDAO.getById(maPhieu, maSanPhamChuan) == null) {
            return false;
        }
        return chiTietHoaDonDAO.delete(maPhieu, maSanPhamChuan);
    }

    public boolean deleteChiTietByHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return false;
        }
        String ma = maHoaDon.trim();
        HoaDon hd = hoaDonDAO.getById(ma);
        if (hd == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(hd.getTrangThai())) {
            return false;
        }
        return chiTietHoaDonDAO.deleteByHoaDon(ma);
    }
}