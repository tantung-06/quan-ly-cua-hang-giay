package bus;

import java.util.ArrayList;

import dao.LoaiGiayDAO;
import dto.LoaiGiay;

public class LoaiGiayBUS {

    private final LoaiGiayDAO loaiGiayDAO = new LoaiGiayDAO();

    public ArrayList<LoaiGiay> getAll() {
        return loaiGiayDAO.getAll();
    }

    public boolean insert(LoaiGiay lg) {
        if (!isDuLieuHopLe(lg)) {
            return false;
        }
        chuanHoaDuLieu(lg);
        return loaiGiayDAO.insert(lg);
    }

    public boolean update(LoaiGiay lg) {
        if (!isDuLieuHopLe(lg)) {
            return false;
        }
        chuanHoaDuLieu(lg);
        return loaiGiayDAO.update(lg);
    }

    private boolean isDuLieuHopLe(LoaiGiay lg) {
        if (lg == null) {
            return false;
        }
        if (lg.getMaLoaiGiay() == null || lg.getMaLoaiGiay().trim().isEmpty()) {
            return false;
        }
        if (lg.getTenLoaiGiay() == null || lg.getTenLoaiGiay().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void chuanHoaDuLieu(LoaiGiay lg) {
        lg.setMaLoaiGiay(lg.getMaLoaiGiay().trim());
        lg.setTenLoaiGiay(lg.getTenLoaiGiay().trim());
    }

    public boolean delete(String maLoaiGiay) {
        if (maLoaiGiay == null || maLoaiGiay.trim().isEmpty()) {
            return false;
        }
        return loaiGiayDAO.delete(maLoaiGiay.trim());
    }
}
