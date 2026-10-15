package bus;

import java.util.ArrayList;

import dao.NhaCungCapDAO;
import dto.NhaCungCap;

public class NhaCungCapBUS {

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

    public ArrayList<NhaCungCap> getAll() {
        return nhaCungCapDAO.getAll();
    }

    public ArrayList<NhaCungCap> getHoatDong() {
        return nhaCungCapDAO.getHoatDong();
    }

    public ArrayList<NhaCungCap> getByTrangThai(String trangThai) {
        if (!isTrangThaiHopLe(trangThai)) {
            return new ArrayList<>();
        }
        return nhaCungCapDAO.getByTrangThai(trangThai.trim());
    }

    public NhaCungCap getById(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            return null;
        }
        return nhaCungCapDAO.getById(maNCC.trim());
    }

    public boolean insert(NhaCungCap ncc) {
        if (!isDuLieuHopLe(ncc)) {
            return false;
        }
        chuanHoaDuLieu(ncc);
        if (!isTrangThaiHopLe(ncc.getTrangThai())) {
            return false;
        }
        return nhaCungCapDAO.insert(ncc);
    }

    public boolean update(NhaCungCap ncc) {
        if (!isDuLieuHopLe(ncc)) {
            return false;
        }
        chuanHoaDuLieu(ncc);
        if (!isTrangThaiHopLe(ncc.getTrangThai())) {
            return false;
        }
        return nhaCungCapDAO.update(ncc);
    }

    private boolean isDuLieuHopLe(NhaCungCap ncc) {
        if (ncc == null) {
            return false;
        }
        if (ncc.getMaNCC() == null || ncc.getMaNCC().trim().isEmpty()) {
            return false;
        }
        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty()) {
            return false;
        }
        if (ncc.getSoDienThoai() == null || ncc.getSoDienThoai().trim().isEmpty()) {
            return false;
        }
        if (ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty()) {
            return false;
        }
        if (ncc.getEmail() == null || ncc.getEmail().trim().isEmpty()) {
            return false;
        }
        if (ncc.getTrangThai() == null || ncc.getTrangThai().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void chuanHoaDuLieu(NhaCungCap ncc) {
        ncc.setMaNCC(ncc.getMaNCC().trim());
        ncc.setTenNCC(ncc.getTenNCC().trim());
        ncc.setSoDienThoai(ncc.getSoDienThoai().trim());
        ncc.setDiaChi(ncc.getDiaChi().trim());
        ncc.setEmail(ncc.getEmail().trim());
        ncc.setTrangThai(ncc.getTrangThai().trim());
    }

    private boolean isTrangThaiHopLe(String trangThai) {
        if (trangThai == null) {
            return false;
        }
        String tt = trangThai.trim();
        return "HOAT_DONG".equals(tt) || "NGUNG".equals(tt);
    }

    public boolean updateTrangThai(String maNCC, String trangThai) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            return false;
        }
        if (!isTrangThaiHopLe(trangThai)) {
            return false;
        }
        return nhaCungCapDAO.updateTrangThai(maNCC.trim(), trangThai.trim());
    }

    public boolean delete(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            return false;
        }
        return nhaCungCapDAO.delete(maNCC.trim());
    }

    public String generateMaNCC() {
        return nhaCungCapDAO.generateMaNCC();
    }
}