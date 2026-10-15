package bus;

import java.util.ArrayList;

import dao.KhachHangDAO;
import dto.KhachHang;

public class KhachHangBUS {

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public ArrayList<KhachHang> getAll() {
        return khachHangDAO.getAll();
    }

    public KhachHang getById(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.getById(maKhachHang.trim());
    }

    public ArrayList<KhachHang> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return khachHangDAO.getByTrangThai(trangThai.trim());
    }

    public ArrayList<KhachHang> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return khachHangDAO.search(keyword.trim());
    }

    public boolean insert(KhachHang kh) {
        if (!isDuLieuHopLe(kh)) {
            return false;
        }
        chuanHoaDuLieu(kh);
        if (!isTrangThaiHopLe(kh.getTrangThai())) {
            return false;
        }
        if (kh.getDiemTichLuy() < 0) {
            return false;
        }
        return khachHangDAO.insert(kh);
    }

    public boolean update(KhachHang kh) {
        if (!isDuLieuHopLe(kh)) {
            return false;
        }
        chuanHoaDuLieu(kh);
        if (!isTrangThaiHopLe(kh.getTrangThai())) {
            return false;
        }
        if (kh.getDiemTichLuy() < 0) {
            return false;
        }
        return khachHangDAO.update(kh);
    }

    private boolean isDuLieuHopLe(KhachHang kh) {
        if (kh == null) {
            return false;
        }
        if (kh.getMaKhachHang() == null || kh.getMaKhachHang().trim().isEmpty()) {
            return false;
        }
        if (kh.getHo() == null || kh.getHo().trim().isEmpty()) {
            return false;
        }
        if (kh.getTen() == null || kh.getTen().trim().isEmpty()) {
            return false;
        }
        if (kh.getSoDienThoai() == null || kh.getSoDienThoai().trim().isEmpty()) {
            return false;
        }
        if (kh.getDiaChi() == null || kh.getDiaChi().trim().isEmpty()) {
            return false;
        }
        if (kh.getEmail() == null || kh.getEmail().trim().isEmpty()) {
            return false;
        }
        if (kh.getTrangThai() == null || kh.getTrangThai().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void chuanHoaDuLieu(KhachHang kh) {
        kh.setMaKhachHang(kh.getMaKhachHang().trim());
        kh.setHo(kh.getHo().trim());
        kh.setTen(kh.getTen().trim());
        kh.setSoDienThoai(kh.getSoDienThoai().trim());
        kh.setDiaChi(kh.getDiaChi().trim());
        kh.setEmail(kh.getEmail().trim());
        kh.setTrangThai(kh.getTrangThai().trim());
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        if (trangThai == null) {
            return false;
        }
        String tt = trangThai.trim();
        return "HOAT_DONG".equals(tt) || "NGUNG".equals(tt);
    }

    public boolean updateTrangThai(String maKhachHang, String trangThai) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        return khachHangDAO.updateTrangThai(maKhachHang.trim(), trangThai.trim());
    }

    public boolean delete(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return false;
        }
        return khachHangDAO.delete(maKhachHang.trim());
    }

    public String generateMaKhachHang() {
        return khachHangDAO.generateMaKhachHang();
    }
}