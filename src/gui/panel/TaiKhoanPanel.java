package gui.panel;

import bus.TaiKhoanBUS;
import dto.TaiKhoan;

public class TaiKhoanPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public TaiKhoanPanel() {
        super("Quản lý tài khoản", "Tài khoản đăng nhập và nhóm quyền được gán.",
                new String[] { "Mã tài khoản", "Tên đăng nhập", "Mã nhóm quyền", "Trạng thái" },
                () -> new TaiKhoanBUS().getAll(),
                value -> {
                    TaiKhoan item = (TaiKhoan) value;
                    return new Object[] { item.getMaTaiKhoan(), item.getTenDangNhap(),
                            item.getMaNhomQuyen(), item.getTrangThai() };
                });
    }
}