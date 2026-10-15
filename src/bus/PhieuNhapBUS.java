package bus;

import java.time.LocalDate;
import java.util.ArrayList;

import dao.PhieuNhapDAO;
import dao.ChiTietPhieuNhapDAO;
import dto.PhieuNhap;
import dto.ChiTietPhieuNhap;

public class PhieuNhapBUS {

    private final PhieuNhapDAO phieuNhapDAO;
    private final ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;

    public PhieuNhapBUS() {
        phieuNhapDAO = new PhieuNhapDAO();
        chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "CHO_XU_LY".equals(trangThai) || "DA_NHAP".equals(trangThai) || "HUY".equals(trangThai);
    }

    private String validatePhieuNhap(PhieuNhap pn) {
        if (pn == null) {
            return "Phiếu nhập không được để trống!";
        }
        if (pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty()) {
            return "Mã phiếu nhập không được để trống!";
        }
        if (pn.getNgayNhap() == null) {
            return "Ngày nhập không được để trống!";
        }
        if (pn.getNgayNhap().isAfter(LocalDate.now())) {
            return "Ngày nhập không được lớn hơn ngày hiện tại!";
        }
        if (pn.getMaNhanVien() == null
                || pn.getMaNhanVien().trim().isEmpty()) {
            return "Vui lòng chọn nhân viên!";
        }
        if (pn.getMaNCC() == null || pn.getMaNCC().trim().isEmpty()) {
            return "Vui lòng chọn nhà cung cấp!";
        }
        if (Double.isNaN(pn.getTongTien()) || Double.isInfinite(pn.getTongTien()) || pn.getTongTien() < 0) {
            return "Tổng tiền không hợp lệ!";
        }
        if (!isTrangThaiHopLe(pn.getTrangThai())) {
            return "Trạng thái phiếu nhập không hợp lệ!";
        }
        return null;
    }

    public ArrayList<PhieuNhap> getAll() {
        return phieuNhapDAO.getAll();
    }

    public PhieuNhap getById(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return null;
        }
        return phieuNhapDAO.getById(maPhieuNhap.trim());
    }

    public ArrayList<PhieuNhap> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return phieuNhapDAO.getByTrangThai(trangThai);
    }

    public ArrayList<PhieuNhap> getByNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return phieuNhapDAO.getByNhanVien(maNhanVien.trim());
    }

    public boolean insert(PhieuNhap pn) {
        String loi = validatePhieuNhap(pn);
        if (loi != null) {
            return false;
        }
        if (phieuNhapDAO.getById(pn.getMaPhieuNhap().trim()) != null) {
            return false;
        }
        return phieuNhapDAO.insert(pn);
    }

    public boolean update(PhieuNhap pn) {
        String loi = validatePhieuNhap(pn);
        if (loi != null) {
            return false;
        }
        if (phieuNhapDAO.getById(pn.getMaPhieuNhap().trim()) == null) {
            return false;
        }
        return phieuNhapDAO.update(pn);
    }

    public boolean updateTrangThai(String maPhieuNhap, String trangThai) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        if (phieuNhapDAO.getById(maPhieuNhap.trim()) == null) {
            return false;
        }
        return phieuNhapDAO.updateTrangThai(maPhieuNhap.trim(), trangThai);
    }

    public boolean delete(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return false;
        }
        if (phieuNhapDAO.getById(maPhieuNhap.trim()) == null) {
            return false;
        }
        chiTietPhieuNhapDAO.deleteByPhieuNhap(maPhieuNhap.trim());
        return phieuNhapDAO.delete(maPhieuNhap.trim());
    }

    public String generateMaPhieuNhap() {
        return phieuNhapDAO.generateMaPhieuNhap();
    }

    public ArrayList<ChiTietPhieuNhap> getChiTietByPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return chiTietPhieuNhapDAO.getByPhieuNhap(maPhieuNhap.trim());
    }

    public ChiTietPhieuNhap getChiTietById(String maPhieuNhap, String maSanPham) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        return chiTietPhieuNhapDAO.getById(maPhieuNhap.trim(), maSanPham.trim());
    }

    private String validateChiTiet(ChiTietPhieuNhap ct) {
        if (ct == null) {
            return "Chi tiết phiếu nhập không được để trống!";
        }
        if (ct.getMaPhieuNhap() == null || ct.getMaPhieuNhap().trim().isEmpty()) {
            return "Mã phiếu nhập không được để trống!";
        }
        if (ct.getMaSanPham() == null || ct.getMaSanPham().trim().isEmpty()) {
            return "Vui lòng chọn biến thể giày!";
        }
        if (ct.getSoLuong() <= 0) {
            return "Số lượng nhập phải lớn hơn 0!";
        }
        if (Double.isNaN(ct.getGiaNhap()) || Double.isInfinite(ct.getGiaNhap()) || ct.getGiaNhap() < 0) {
            return "Giá nhập không hợp lệ!";
        }
        if (Double.isNaN(ct.getThanhTien()) || Double.isInfinite(ct.getThanhTien()) || ct.getThanhTien() < 0) {
            return "Thành tiền không hợp lệ!";
        }
        double thanhTienDuKien = (double) ct.getSoLuong() * ct.getGiaNhap();
        if (Math.abs(ct.getThanhTien() - thanhTienDuKien) > 0.01) {
            return "Thành tiền phải bằng số lượng nhân giá nhập!";
        }
        return null;
    }

    public boolean insertChiTiet(ChiTietPhieuNhap ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        PhieuNhap pn = phieuNhapDAO.getById(ct.getMaPhieuNhap().trim());
        if (pn == null) {
            return false;
        }
        if ("DA_NHAP".equals(pn.getTrangThai()) || "HUY".equals(pn.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuNhapDAO.getById(
                ct.getMaPhieuNhap().trim(),
                ct.getMaSanPham().trim()) != null) {
            return false;
        }
        return chiTietPhieuNhapDAO.insert(ct);
    }

    public boolean insertBatchChiTiet(ArrayList<ChiTietPhieuNhap> ds) {
        if (ds == null || ds.isEmpty()) {
            return false;
        }
        String maPhieuNhap = ds.get(0).getMaPhieuNhap();
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return false;
        }
        PhieuNhap pn = phieuNhapDAO.getById(maPhieuNhap.trim());
        if (pn == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(pn.getTrangThai())) {
            return false;
        }
        ArrayList<String> maSanPhamDaCo = new ArrayList<>();
        for (ChiTietPhieuNhap ct : ds) {
            String loi = validateChiTiet(ct);
            if (loi != null) {
                return false;
            }
            if (!maPhieuNhap.trim().equals(ct.getMaPhieuNhap().trim())) {
                return false;
            }
            String maSanPham = ct.getMaSanPham().trim();
            if (maSanPhamDaCo.contains(maSanPham)) {
                return false;
            }
            maSanPhamDaCo.add(maSanPham);
            if (chiTietPhieuNhapDAO.getById(maPhieuNhap.trim(), maSanPham) != null) {
                return false;
            }
        }
        return chiTietPhieuNhapDAO.insertBatch(ds);
    }

    public boolean updateChiTiet(ChiTietPhieuNhap ct) {
        String loi = validateChiTiet(ct);
        if (loi != null) {
            return false;
        }
        PhieuNhap pn = phieuNhapDAO.getById(ct.getMaPhieuNhap().trim());
        if (pn == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(pn.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuNhapDAO.getById(ct.getMaPhieuNhap().trim(), ct.getMaSanPham().trim()) == null) {
            return false;
        }
        return chiTietPhieuNhapDAO.update(ct);
    }

    public boolean deleteChiTiet(String maPhieuNhap, String maSanPham) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty() || maSanPham == null || maSanPham.trim().isEmpty()) {
            return false;
        }
        PhieuNhap pn = phieuNhapDAO.getById(maPhieuNhap.trim());
        if (pn == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(pn.getTrangThai())) {
            return false;
        }
        if (chiTietPhieuNhapDAO.getById(maPhieuNhap.trim(), maSanPham.trim()) == null) {
            return false;
        }
        return chiTietPhieuNhapDAO.delete(maPhieuNhap.trim(), maSanPham.trim());
    }

    public boolean deleteChiTietByPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return false;
        }
        PhieuNhap pn = phieuNhapDAO.getById(maPhieuNhap.trim());
        if (pn == null) {
            return false;
        }
        if (!"CHO_XU_LY".equals(pn.getTrangThai())) {
            return false;
        }
        return chiTietPhieuNhapDAO.deleteByPhieuNhap(maPhieuNhap.trim());
    }
}