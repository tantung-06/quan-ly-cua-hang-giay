package bus;

import java.util.ArrayList;
import java.util.Objects;

import dao.SanPhamDAO;
import dto.SanPham;

public class SanPhamBUS {

    private final SanPhamDAO sanPhamDAO;

    public SanPhamBUS() {
        this(new SanPhamDAO());
    }

    public SanPhamBUS(SanPhamDAO sanPhamDAO) {
        this.sanPhamDAO = Objects.requireNonNull(sanPhamDAO);
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "DANG_BAN".equals(trangThai) || "NGUNG_BAN".equals(trangThai);
    }

    public ArrayList<SanPham> getAll() {
        return sanPhamDAO.getAll();
    }

    public SanPham getById(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        return sanPhamDAO.getById(maSanPham.trim());
    }

    public ArrayList<SanPham> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return sanPhamDAO.getByTrangThai(trangThai);
    }

    public ArrayList<SanPham> getByThuongHieu(String maThuongHieu) {
        if (maThuongHieu == null || maThuongHieu.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return sanPhamDAO.getByThuongHieu(maThuongHieu.trim());
    }

    public ArrayList<SanPham> getByLoaiGiay(String maLoaiGiay) {
        if (maLoaiGiay == null || maLoaiGiay.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return sanPhamDAO.getByLoaiGiay(maLoaiGiay.trim());
    }

    public ArrayList<SanPham> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return sanPhamDAO.search(keyword.trim());
    }

    public boolean insert(SanPham s) {
        if (s == null || !isDuLieuHopLe(s)) {
            return false;
        }
        chuanHoaDuLieu(s);
        return sanPhamDAO.insert(s);
    }

    public boolean update(SanPham s) {
        if (s == null || !isDuLieuHopLe(s)) {
            return false;
        }
        chuanHoaDuLieu(s);
        return sanPhamDAO.update(s);
    }

    private boolean isDuLieuHopLe(SanPham s) {
        if (s.getMaSanPham() == null || s.getMaSanPham().trim().isEmpty()) {
            return false;
        }
        if (s.getTenSanPham() == null || s.getTenSanPham().trim().isEmpty()) {
            return false;
        }
        if (s.getMaLoaiGiay() == null || s.getMaLoaiGiay().trim().isEmpty()) {
            return false;
        }
        if (s.getMaThuongHieu() == null || s.getMaThuongHieu().trim().isEmpty()) {
            return false;
        }
        if (!Double.isFinite(s.getGiaBan()) || s.getGiaBan() < 0 || s.getSoLuongTon() < 0) {
            return false;
        }
        return isTrangThaiHopLe(s.getTrangThai());
    }

    public boolean delete(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return false;
        }
        return sanPhamDAO.delete(maSanPham.trim());
    }

    public boolean updateTrangThai(String maSanPham, String trangThai) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        return sanPhamDAO.updateTrangThai(maSanPham.trim(), trangThai);
    }

    public String generateMaSanPham() {
        return sanPhamDAO.generateMaSanPham();
    }

    private void chuanHoaDuLieu(SanPham s) {
        s.setMaSanPham(s.getMaSanPham().trim());
        s.setTenSanPham(s.getTenSanPham().trim());
        s.setMaLoaiGiay(s.getMaLoaiGiay().trim());
        s.setMaThuongHieu(s.getMaThuongHieu().trim());
    }
}