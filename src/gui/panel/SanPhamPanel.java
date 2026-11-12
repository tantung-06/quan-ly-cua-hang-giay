package gui.panel;

import bus.SanPhamBUS;
import dto.SanPham;

public class SanPhamPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public SanPhamPanel() {
        super("Danh mục sản phẩm", "Theo dõi thông tin và tồn kho sản phẩm.",
                new String[] { "Mã sản phẩm", "Tên sản phẩm", "Loại giày", "Thương hiệu", "Giá bán", "Tồn kho",
                        "Trạng thái" },
                () -> new SanPhamBUS().getAll(),
                value -> {
                    SanPham item = (SanPham) value;
                    return new Object[] { item.getMaSanPham(), item.getTenSanPham(), item.getMaLoaiGiay(),
                            item.getMaThuongHieu(), item.getGiaBan(), item.getSoLuongTon(), item.getTrangThai() };
                });
    }
}