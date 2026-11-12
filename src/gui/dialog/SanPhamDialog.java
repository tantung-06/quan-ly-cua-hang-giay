package gui.dialog;

public final class SanPhamDialog extends FormDialog {
    public SanPhamDialog(String[] values, boolean editing) {
        this(values, editing, null, null);
    }

    public SanPhamDialog(String[] values, boolean editing, String[] typeOptions, String[] brandOptions) {
        super(new String[] { "Mã sản phẩm", "Tên sản phẩm", "Loại giày", "Thương hiệu",
                "Giá bán", "Số lượng tồn", "Trạng thái" },
                values,
                new String[][] { null, null, typeOptions, brandOptions, null, null,
                        { "DANG_BAN", "NGUNG_BAN" } },
                new boolean[] { true, false, false, false, false, false, false });
    }
}