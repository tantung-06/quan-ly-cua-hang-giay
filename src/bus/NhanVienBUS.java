package bus;

import java.util.ArrayList;

import dao.NhanVienDAO;
import dto.NhanVien;

public class NhanVienBUS {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public ArrayList<NhanVien> getAll() {
        return nhanVienDAO.getAll();
    }

    public NhanVien getById(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return null;
        }
        return nhanVienDAO.getById(maNhanVien.trim());
    }

    public NhanVien getByTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return null;
        }
        return nhanVienDAO.getByTaiKhoan(maTaiKhoan.trim());
    }

    public ArrayList<NhanVien> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return nhanVienDAO.search(keyword.trim());
    }

    public boolean insert(NhanVien nv) {
        if (!isDuLieuHopLe(nv)) {
            return false;
        }
        nv.setMaNhanVien(nv.getMaNhanVien().trim());
        nv.setHo(nv.getHo().trim());
        nv.setTen(nv.getTen().trim());
        nv.setSoDienThoai(nv.getSoDienThoai().trim());
        nv.setDiaChi(nv.getDiaChi().trim());
        nv.setEmail(nv.getEmail().trim());
        nv.setMaTaiKhoan(nv.getMaTaiKhoan().trim());
        nv.setTrangThai(nv.getTrangThai().trim());
        if (!isTrangThaiHopLe(nv.getTrangThai())) {
            return false;
        }
        return nhanVienDAO.insert(nv);
    }

    public boolean update(NhanVien nv) {
        if (!isDuLieuHopLe(nv)) {
            return false;
        }
        nv.setMaNhanVien(nv.getMaNhanVien().trim());
        nv.setHo(nv.getHo().trim());
        nv.setTen(nv.getTen().trim());
        nv.setSoDienThoai(nv.getSoDienThoai().trim());
        nv.setDiaChi(nv.getDiaChi().trim());
        nv.setEmail(nv.getEmail().trim());
        nv.setMaTaiKhoan(nv.getMaTaiKhoan().trim());
        nv.setTrangThai(nv.getTrangThai().trim());
        if (!isTrangThaiHopLe(nv.getTrangThai())) {
            return false;
        }
        return nhanVienDAO.update(nv);
    }

    private boolean isDuLieuHopLe(NhanVien nv) {
        if (nv == null) {
            return false;
        }
        if (nv.getMaNhanVien() == null || nv.getMaNhanVien().trim().isEmpty()) {
            return false;
        }
        if (nv.getHo() == null || nv.getHo().trim().isEmpty()) {
            return false;
        }
        if (nv.getTen() == null || nv.getTen().trim().isEmpty()) {
            return false;
        }
        if (nv.getSoDienThoai() == null || nv.getSoDienThoai().trim().isEmpty()) {
            return false;
        }
        if (nv.getDiaChi() == null || nv.getDiaChi().trim().isEmpty()) {
            return false;
        }
        if (nv.getEmail() == null || nv.getEmail().trim().isEmpty()) {
            return false;
        }
        if (nv.getMaTaiKhoan() == null || nv.getMaTaiKhoan().trim().isEmpty()) {
            return false;
        }
        if (nv.getTrangThai() == null || nv.getTrangThai().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "HOAT_DONG".equals(trangThai) || "NGUNG".equals(trangThai);
    }

    public boolean delete(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return false;
        }
        return nhanVienDAO.delete(maNhanVien.trim());
    }

    public ArrayList<NhanVien> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return nhanVienDAO.getByTrangThai(trangThai.trim());
    }

    public boolean updateTrangThai(
            String maNhanVien, String trangThai) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return false;
        }
        if (trangThai == null || !isTrangThaiHopLe(trangThai.trim())) {
            return false;
        }
        return nhanVienDAO.updateTrangThai(maNhanVien.trim(), trangThai.trim());
    }

    public String generateMaNhanVien() {
        return nhanVienDAO.generateMaNhanVien();
    }
}