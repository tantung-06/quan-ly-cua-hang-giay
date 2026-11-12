package bus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import dao.ChiTietPhieuXuatDAO;
import dao.KhachHangDAO;
import dao.PhieuXuatDAO;
import dto.ChiTietPhieuXuat;
import dto.KhachHang;
import dto.KhuyenMai;
import dto.PhieuXuat;

public class PhieuXuatBUS {

    private final PhieuXuatDAO phieuXuatDAO;
    private final ChiTietPhieuXuatDAO chiTietPhieuXuatDAO;
    private final KhuyenMaiBUS khuyenMaiBUS;
    private final KhachHangDAO customers;

    public PhieuXuatBUS() {
        this(new PhieuXuatDAO(), new ChiTietPhieuXuatDAO(), new KhuyenMaiBUS());
    }

    public PhieuXuatBUS(PhieuXuatDAO phieuXuatDAO, ChiTietPhieuXuatDAO chiTietPhieuXuatDAO,
            KhuyenMaiBUS khuyenMaiBUS) {
        this(phieuXuatDAO, chiTietPhieuXuatDAO, khuyenMaiBUS, new KhachHangDAO());
    }

    public PhieuXuatBUS(PhieuXuatDAO phieuXuatDAO, ChiTietPhieuXuatDAO chiTietPhieuXuatDAO,
            KhuyenMaiBUS khuyenMaiBUS, KhachHangDAO customers) {
        this.phieuXuatDAO = Objects.requireNonNull(phieuXuatDAO);
        this.chiTietPhieuXuatDAO = Objects.requireNonNull(chiTietPhieuXuatDAO);
        this.khuyenMaiBUS = Objects.requireNonNull(khuyenMaiBUS);
        this.customers = Objects.requireNonNull(customers);
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "CHO_XU_LY".equals(trangThai) || "DA_XUAT".equals(trangThai) || "HUY".equals(trangThai);
    }

    private String validatePhieuXuat(PhieuXuat px) {
        if (px == null) {
            return "Phiếu xuất không được để trống!";
        }
        if (px.getMaPhieuXuat() == null || px.getMaPhieuXuat().trim().isEmpty()) {
            return "Mã phiếu xuất không được để trống!";
        }
        if (px.getNgayLap() == null) {
            return "Ngày lập phiếu xuất không được để trống!";
        }
        if (px.getNgayLap().isAfter(LocalDate.now())) {
            return "Ngày lập phiếu xuất không được lớn hơn ngày hiện tại!";
        }
        if (px.getMaNhanVien() == null || px.getMaNhanVien().trim().isEmpty()) {
            return "Vui lòng chọn nhân viên lập phiếu xuất!";
        }
        if (px.getPhuongThucThanhToan() == null || px.getPhuongThucThanhToan().trim().isEmpty()) {
            return "Phương thức thanh toán không được để trống!";
        }
        if (!Double.isFinite(px.getTongTien()) || px.getTongTien() < 0) {
            return "Tổng tiền không hợp lệ!";
        }
        if (!isTrangThaiHopLe(px.getTrangThai())) {
            return "Trạng thái phiếu xuất không hợp lệ!";
        }
        if (px.getDiemSuDung() < 0 || px.getDiemDuocCong() < 0
                || (px.getMaKhachHang() == null && px.getDiemSuDung() != 0)) {
            return "Điểm sử dụng không hợp lệ hoặc chưa chọn khách hàng.";
        }
        return null;
    }

    public ArrayList<PhieuXuat> getAll() {
        return phieuXuatDAO.getAll();
    }

    public PhieuXuat getById(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return null;
        }
        return phieuXuatDAO.getById(maPhieuXuat.trim());
    }

    public ArrayList<PhieuXuat> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return phieuXuatDAO.getByTrangThai(trangThai);
    }

    public ArrayList<PhieuXuat> getByKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return phieuXuatDAO.getByKhachHang(maKhachHang.trim());
    }

    public ArrayList<PhieuXuat> getByNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return phieuXuatDAO.getByNhanVien(maNhanVien.trim());
    }

    public boolean insert(PhieuXuat px) {
        String loi = validatePhieuXuat(px);
        if (loi != null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai()) || px.isDiemDaXuLy()
                || px.getDiemDuocCong() != 0 || px.getDiemSuDung() != 0) {
            return false;
        }
        if (phieuXuatDAO.getById(px.getMaPhieuXuat().trim()) != null) {
            return false;
        }
        return phieuXuatDAO.insert(px);
    }

    public boolean lapPhieu(PhieuXuat px, ArrayList<ChiTietPhieuXuat> details) {
        if (validatePhieuXuat(px) != null || !"CHO_XU_LY".equals(px.getTrangThai())
                || px.isDiemDaXuLy() || px.getDiemDuocCong() != 0
                || details == null || details.isEmpty()) {
            return false;
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        Set<String> productIds = new HashSet<>();
        for (ChiTietPhieuXuat detail : details) {
            if (validateChiTiet(detail) != null
                    || !px.getMaPhieuXuat().equals(detail.getMaPhieuXuat())
                    || !productIds.add(detail.getMaSanPham())) {
                return false;
            }
            subtotal = subtotal.add(BigDecimal.valueOf(detail.getDonGia())
                    .multiply(BigDecimal.valueOf(detail.getSoLuong())));
        }
        KhuyenMai promotion = null;
        if (px.getMaKhuyenMai() != null && !px.getMaKhuyenMai().trim().isEmpty()) {
            khuyenMaiBUS.kiemTraLuotSuDung(px.getMaKhachHang(), px.getMaKhuyenMai());
            promotion = khuyenMaiBUS.getById(px.getMaKhuyenMai());
            if (promotion == null) {
                throw new IllegalArgumentException("Khuyến mãi đã chọn không còn tồn tại. Vui lòng chọn lại.");
            }
            px.setMaKhuyenMai(promotion.getMaKhuyenMai());
        } else {
            px.setMaKhuyenMai(null);
        }
        double payable = khuyenMaiBUS.tinhTongThanhToan(promotion, subtotal.doubleValue(), px.getNgayLap());
        int availablePoints = 0;
        if (px.getMaKhachHang() != null) {
            KhachHang customer = customers.getById(px.getMaKhachHang());
            if (customer == null || !"HOAT_DONG".equals(customer.getTrangThai())) {
                throw new IllegalArgumentException("Khách hàng không tồn tại hoặc đã ngừng hoạt động.");
            }
            availablePoints = customer.getDiemTichLuy();
            px.setMaKhachHang(customer.getMaKhachHang());
        }
        payable = DiemTichLuyBUS.tinhThucTra(payable, px.getDiemSuDung(), availablePoints);
        if (Math.abs(payable - px.getTongTien()) > 0.005) {
            throw new IllegalArgumentException(
                    "Tổng thanh toán đã thay đổi. Vui lòng kiểm tra khuyến mãi và điểm sử dụng.");
        }
        px.setTongTien(payable);
        return phieuXuatDAO.insertWithDetails(px, details);
    }

    public boolean update(PhieuXuat px) {
        String loi = validatePhieuXuat(px);
        if (loi != null) {
            return false;
        }
        if (phieuXuatDAO.getById(px.getMaPhieuXuat().trim()) == null) {
            return false;
        }
        return phieuXuatDAO.update(px);
    }

    public boolean updateTrangThai(String maPhieuXuat, String trangThai) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return false;
        }
        if ("DA_XUAT".equals(trangThai)) {
            return hoanTatXuat(maPhieuXuat);
        }
        if ("HUY".equals(trangThai)) {
            return huyPhieu(maPhieuXuat);
        }
        return false;
    }

    public boolean huyPhieu(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return false;
        }
        return phieuXuatDAO.huyPhieu(maPhieuXuat.trim());
    }

    public boolean hoanTatXuat(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return false;
        }
        return phieuXuatDAO.xuatKho(maPhieuXuat.trim());
    }

    public boolean delete(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return false;
        }
        String ma = maPhieuXuat.trim();
        PhieuXuat px = phieuXuatDAO.getById(ma);
        if (px == null) {
            return false;
        }
        if ("DA_XUAT".equals(px.getTrangThai()) || px.isDiemDaXuLy()) {
            return false;
        }
        if (!chiTietPhieuXuatDAO.deleteByPhieuXuat(ma)) {
            return false;
        }
        return phieuXuatDAO.delete(ma);
    }

    public String generateMaPhieuXuat() {
        return phieuXuatDAO.generateMaPhieuXuat();
    }

    public ArrayList<ChiTietPhieuXuat> getChiTietByPhieuXuat(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return chiTietPhieuXuatDAO.getByPhieuXuat(
                maPhieuXuat.trim());
    }

    public ChiTietPhieuXuat getChiTietById(String maPhieuXuat, String maSanPham) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        return chiTietPhieuXuatDAO.getById(maPhieuXuat.trim(), maSanPham.trim());
    }

    private String validateChiTiet(ChiTietPhieuXuat ct) {
        if (ct == null) {
            return "Chi tiết phiếu xuất không được để trống!";
        }
        if (ct.getMaPhieuXuat() == null || ct.getMaPhieuXuat().trim().isEmpty()) {
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

    public boolean insertChiTiet(ChiTietPhieuXuat ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        String maPhieuXuat = ct.getMaPhieuXuat().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieuXuat);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuXuatDAO.getById(maPhieuXuat, ct.getMaSanPham().trim()) != null) {
            return false;
        }
        return chiTietPhieuXuatDAO.insert(ct);
    }

    public boolean insertBatchChiTiet(ArrayList<ChiTietPhieuXuat> ds) {
        if (ds == null || ds.isEmpty()) {
            return false;
        }
        ChiTietPhieuXuat dauTien = ds.get(0);
        if (dauTien == null || dauTien.getMaPhieuXuat() == null || dauTien.getMaPhieuXuat().trim().isEmpty()) {
            return false;
        }
        String maPhieuXuat = dauTien.getMaPhieuXuat().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieuXuat);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        Set<String> maSanPhamDaCo = new HashSet<>();
        for (ChiTietPhieuXuat ct : ds) {
            String loi = validateChiTiet(ct);
            if (loi != null) {
                return false;
            }
            if (!maPhieuXuat.equals(ct.getMaPhieuXuat().trim())) {
                return false;
            }
            String maSanPham = ct.getMaSanPham().trim();
            if (!maSanPhamDaCo.add(maSanPham)) {
                return false;
            }
            if (chiTietPhieuXuatDAO.getById(maPhieuXuat, maSanPham) != null) {
                return false;
            }
        }
        return chiTietPhieuXuatDAO.insertBatch(ds);
    }

    public boolean updateChiTiet(ChiTietPhieuXuat ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        String maPhieuXuat = ct.getMaPhieuXuat().trim();
        String maSanPham = ct.getMaSanPham().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieuXuat);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuXuatDAO.getById(maPhieuXuat, maSanPham) == null) {
            return false;
        }
        return chiTietPhieuXuatDAO.update(ct);
    }

    public boolean deleteChiTiet(String maPhieuXuat, String maSanPham) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return false;
        }
        String maPhieu = maPhieuXuat.trim();
        String maSanPhamChuan = maSanPham.trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieu);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuXuatDAO.getById(maPhieu, maSanPhamChuan) == null) {
            return false;
        }
        return chiTietPhieuXuatDAO.delete(maPhieu, maSanPhamChuan);
    }

    public boolean deleteChiTietByPhieuXuat(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            return false;
        }
        String ma = maPhieuXuat.trim();
        PhieuXuat px = phieuXuatDAO.getById(ma);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        return chiTietPhieuXuatDAO.deleteByPhieuXuat(ma);
    }
}