package bus;

import java.util.ArrayList;

import dao.NhomQuyenDAO;
import dto.NhomQuyen;
import security.AccessPolicy;

public class NhomQuyenBUS {

    private final NhomQuyenDAO nhomQuyenDAO = new NhomQuyenDAO();

    public ArrayList<NhomQuyen> getAll() {
        return nhomQuyenDAO.getAll();
    }

    public NhomQuyen getByID(String maNhomQuyen) {
        if (maNhomQuyen == null || maNhomQuyen.trim().isEmpty()) {
            return null;
        }
        return nhomQuyenDAO.getByID(maNhomQuyen.trim());
    }

    public boolean insert(NhomQuyen nq) {
        if (!isDuLieuHopLe(nq)) {
            return false;
        }
        nq.setMaNhomQuyen(nq.getMaNhomQuyen().trim());
        nq.setTenNhomQuyen(nq.getTenNhomQuyen().trim());
        normalizePermissions(nq);
        return nhomQuyenDAO.insert(nq);
    }

    public boolean update(NhomQuyen nq) {
        if (!isDuLieuHopLe(nq)) {
            return false;
        }
        nq.setMaNhomQuyen(nq.getMaNhomQuyen().trim());
        nq.setTenNhomQuyen(nq.getTenNhomQuyen().trim());
        normalizePermissions(nq);
        return nhomQuyenDAO.update(nq);
    }

    private boolean isDuLieuHopLe(NhomQuyen nq) {
        if (nq == null) {
            return false;
        }
        if (nq.getMaNhomQuyen() == null || nq.getMaNhomQuyen().trim().isEmpty()) {
            return false;
        }
        if (nq.getTenNhomQuyen() == null || nq.getTenNhomQuyen().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void normalizePermissions(NhomQuyen group) {
        group.setQuyenHan(AccessPolicy.encode(
                AccessPolicy.effectivePermissions(group.getMaNhomQuyen(), group.getQuyenHan())));
    }

    public boolean delete(String maNhomQuyen) {
        if (maNhomQuyen == null || maNhomQuyen.trim().isEmpty()) {
            return false;
        }
        if (AccessPolicy.ADMIN_ROLE.equals(maNhomQuyen.trim())) {
            throw new IllegalArgumentException("Không thể xóa nhóm Quản trị viên.");
        }
        return nhomQuyenDAO.delete(maNhomQuyen.trim());
    }

    public String generateMaNhomQuyen() {
        return nhomQuyenDAO.generateMaNhomQuyen();
    }
}
