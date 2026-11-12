package gui.dialog;

public final class NhanVienDialog extends FormDialog {
    public NhanVienDialog(String[] values, boolean editing) {
        super(new String[] { "Mã nhân viên", "Họ", "Tên", "Điện thoại",
                "Địa chỉ", "Email", "Mã tài khoản", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, null, null, null, { "HOAT_DONG", "NGUNG" } },
                new boolean[] { true, false, false, false, false, false, false, false });
    }
}