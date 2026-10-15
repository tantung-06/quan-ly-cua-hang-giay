package bus;

import java.util.ArrayList;

import dao.ThuongHieuDAO;
import dto.ThuongHieu;

public class ThuongHieuBUS {

    private ThuongHieuDAO thuongHieuDAO = new ThuongHieuDAO();

    public ArrayList<ThuongHieu> getAll() {
        return thuongHieuDAO.getAll();
    }

    public boolean insert(ThuongHieu th) {
        if (th == null) {
            return false;
        }
        if (th.getMaThuongHieu() == null || th.getMaThuongHieu().trim().isEmpty()) {
            return false;
        }
        if (th.getTenThuongHieu() == null || th.getTenThuongHieu().trim().isEmpty()) {
            return false;
        }
        th.setMaThuongHieu(th.getMaThuongHieu().trim());
        th.setTenThuongHieu(th.getTenThuongHieu().trim());
        return thuongHieuDAO.insert(th);
    }

    public boolean update(ThuongHieu th) {
        if (th == null) {
            return false;
        }
        if (th.getMaThuongHieu() == null || th.getMaThuongHieu().trim().isEmpty()) {
            return false;
        }
        if (th.getTenThuongHieu() == null || th.getTenThuongHieu().trim().isEmpty()) {
            return false;
        }
        th.setMaThuongHieu(th.getMaThuongHieu().trim());
        th.setTenThuongHieu(th.getTenThuongHieu().trim());
        return thuongHieuDAO.update(th);
    }

    public boolean delete(String maThuongHieu) {
        if (maThuongHieu == null || maThuongHieu.trim().isEmpty()) {
            return false;
        }
        return thuongHieuDAO.delete(maThuongHieu.trim());
    }
}