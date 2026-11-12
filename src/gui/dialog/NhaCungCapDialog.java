package gui.dialog;

public final class NhaCungCapDialog extends FormDialog {
    public NhaCungCapDialog(String[] values, boolean editing) {
        super(new String[] { "Mã nhà cung cấp", "Tên nhà cung cấp", "Điện thoại",
                "Địa chỉ", "Email", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, null, { "HOAT_DONG", "NGUNG" } },
                new boolean[] { true, false, false, false, false, false });
    }
}