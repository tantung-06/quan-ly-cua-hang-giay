package gui.panel;

import bus.NhomQuyenBUS;
import dto.NhomQuyen;

public class NhomQuyenPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public NhomQuyenPanel() {
        super("Nhóm quyền", "Các nhóm quyền được cấu hình trong hệ thống.",
                new String[] { "Mã nhóm quyền", "Tên nhóm quyền" },
                () -> new NhomQuyenBUS().getAll(),
                value -> {
                    NhomQuyen item = (NhomQuyen) value;
                    return new Object[] { item.getMaNhomQuyen(), item.getTenNhomQuyen() };
                });
    }
}