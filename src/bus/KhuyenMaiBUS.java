package bus;

import java.time.LocalDate;
import java.util.ArrayList;

import dao.KhuyenMaiDAO;
import dto.KhuyenMai;

public class KhuyenMaiBUS {

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    public ArrayList<KhuyenMai> getAll() {
        return khuyenMaiDAO.getAll();
    }

    public ArrayList<KhuyenMai> getApDung() {
        return khuyenMaiDAO.getApDung();
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
        if (km.getDonHangToiThieu() < 0 || km.getMucGiam() <= 0) {
            return false;
        }
        if (km.getTrangThai() == null || km.getTrangThai().trim().isEmpty()) {
            return false;
        }
        String loai = km.getLoaiKhuyenMai().trim();
        if (!"PHAN_TRAM".equals(loai) && !"TIEN_MAT".equals(loai)) {
            return false;
        }
        if ("PHAN_TRAM".equals(loai) && km.getMucGiam() > 100) {
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
        return "AP_DUNG".equals(tt) || "NGUNG".equals(tt);
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