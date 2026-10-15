package bus;

import java.time.LocalDate;
import java.util.ArrayList;

import dao.PhieuXuatDAO;
import dao.ChiTietPhieuXuatDAO;
import dto.PhieuXuat;
import dto.ChiTietPhieuXuat;

public class PhieuXuatBUS {

    private final PhieuXuatDAO phieuXuatDAO;
    private final ChiTietPhieuXuatDAO chiTietPhieuXuatDAO;

    public PhieuXuatBUS() {
        phieuXuatDAO = new PhieuXuatDAO();
        chiTietPhieuXuatDAO = new ChiTietPhieuXuatDAO();
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "CHO_XU_LY".equals(trangThai) || "DA_XUAT".equals(trangThai) || "HUY".equals(trangThai);
    }

    private String validatePhieuXuat(PhieuXuat px) {
        if (px == null) {
        }
        if (px.getMaPhieuXuat() == null || px.getMaPhieuXuat().trim().isEmpty()) {
            return "Mã phiếu xuất không được để trống!";
        }
        if (px.getNgayLap() == null) {
            return "Ngày lập phiếu không được để trống!";
        }
        if (px.getNgayLap().isAfter(LocalDate.now())) {
            return "Ngày lập phiếu không được lớn hơn ngày hiện tại!";
        }
        if (px.getMaNhanVien() == null || px.getMaNhanVien().trim().isEmpty()) {
            return "Vui lòng chọn nhân viên lập phiếu!";
        }
        if (px.getMaKhachHang() == null || px.getMaKhachHang().trim().isEmpty()) {
            return "Vui lòng chọn khách hàng!";
        }
        if (px.getPhuongThucThanhToan() == null || px.getPhuongThucThanhToan().trim().isEmpty()) {
            return "Phương thức thanh toán không được để trống!";
        }
        if (Double.isNaN(px.getTongTien()) || Double.isInfinite(px.getTongTien()) || px.getTongTien() < 0) {
            return "Tổng tiền không hợp lệ!";
        }
        if (!isTrangThaiHopLe(px.getTrangThai())) {
            return "Trạng thái phiếu xuất không hợp lệ!";
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
        if (phieuXuatDAO.getById(px.getMaPhieuXuat().trim()) != null) {
            return false;
        }
        return phieuXuatDAO.insert(px);
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
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        if (phieuXuatDAO.getById(maPhieuXuat.trim()) == null) {
            return false;
        }
        return phieuXuatDAO.updateTrangThai(maPhieuXuat.trim(), trangThai);
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
        if ("DA_XUAT".equals(px.getTrangThai())) {
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
            return "Vui lòng chọn biến thể giày!";
        }
        if (ct.getSoLuong() <= 0) {
            return "Số lượng xuất phải lớn hơn 0!";
        }
        if (Double.isNaN(ct.getDonGia()) || Double.isInfinite(ct.getDonGia()) || ct.getDonGia() < 0) {
            return "Đơn giá không hợp lệ!";
        }
        if (Double.isNaN(ct.getThanhTien()) || Double.isInfinite(ct.getThanhTien()) || ct.getThanhTien() < 0) {
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
        String maPhieu = ct.getMaPhieuXuat().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieu);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuXuatDAO.getById(maPhieu, ct.getMaSanPham().trim()) != null) {
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
        String maPhieu = dauTien.getMaPhieuXuat().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieu);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        ArrayList<String> maSanPhamDaCo = new ArrayList<>();
        for (ChiTietPhieuXuat ct : ds) {
            String loi = validateChiTiet(ct);
            if (loi != null) {
                return false;
            }
            if (!maPhieu.equals(ct.getMaPhieuXuat().trim())) {
                return false;
            }
            String maSanPham = ct.getMaSanPham().trim();
            if (maSanPhamDaCo.contains(maSanPham)) {
                return false;
            }
            maSanPhamDaCo.add(maSanPham);
            if (chiTietPhieuXuatDAO.getById(maPhieu, maSanPham) != null) {
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
        String maPhieu = ct.getMaPhieuXuat().trim();
        String maSanPham = ct.getMaSanPham().trim();
        PhieuXuat px = phieuXuatDAO.getById(maPhieu);
        if (px == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(px.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuXuatDAO.getById(maPhieu, maSanPham) == null) {
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