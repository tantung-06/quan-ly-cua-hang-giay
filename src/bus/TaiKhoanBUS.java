package bus;

import java.util.ArrayList;

import dao.TaiKhoanDAO;
import dto.TaiKhoan;

public class TaiKhoanBUS {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public ArrayList<TaiKhoan> getAll() {
        return taiKhoanDAO.getAll();
    }

    public TaiKhoan getById(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return null;
        }

        return taiKhoanDAO.getById(maTaiKhoan.trim());
    }

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || matKhau == null || matKhau.isEmpty()) {
            return null;
        }
        return taiKhoanDAO.login(tenDangNhap.trim(), matKhau);
    }

    public boolean insert(TaiKhoan tk) {
        if (!isDuLieuHopLe(tk)) {
            return false;
        }
        chuanHoaDuLieu(tk);
        return taiKhoanDAO.insert(tk);
    }

    public boolean update(TaiKhoan tk) {
        if (!isDuLieuHopLe(tk)) {
            return false;
        }
        chuanHoaDuLieu(tk);
        return taiKhoanDAO.update(tk);
    }

    public boolean delete(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return false;
        }
        return taiKhoanDAO.delete(maTaiKhoan.trim());
    }

    public ArrayList<TaiKhoan> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return taiKhoanDAO.getByTrangThai(trangThai);
    }

    public boolean updateTrangThai(String maTaiKhoan, String trangThai) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        return taiKhoanDAO.updateTrangThai(maTaiKhoan.trim(), trangThai);
    }

    public String generateMaTaiKhoan() {
        return taiKhoanDAO.generateMaTaiKhoan();
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        return "HOAT_DONG".equals(trangThai) || "NGUNG".equals(trangThai);
    }

    private boolean isDuLieuHopLe(TaiKhoan tk) {
        if (tk == null) {
            return false;
        }
        if (tk.getMaTaiKhoan() == null || tk.getMaTaiKhoan().trim().isEmpty()) {
            return false;
        }
        if (tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().isEmpty()) {
            return false;
        }
        if (tk.getMatKhau() == null || tk.getMatKhau().trim().isEmpty()) {
            return false;
        }
        if (tk.getMaNhomQuyen() == null || tk.getMaNhomQuyen().trim().isEmpty()) {
            return false;
        }
        return isTrangThaiHopLe(tk.getTrangThai());
    }

    private void chuanHoaDuLieu(TaiKhoan tk) {
        tk.setMaTaiKhoan(tk.getMaTaiKhoan().trim());
        tk.setTenDangNhap(tk.getTenDangNhap().trim());
        tk.setMaNhomQuyen(tk.getMaNhomQuyen().trim());
    }
}