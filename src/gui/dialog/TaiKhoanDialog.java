package gui.dialog;

public final class TaiKhoanDialog extends FormDialog {
    public TaiKhoanDialog(String[] values, boolean editing) {
        super(new String[] { "Mã tài khoản", "Tên đăng nhập", "Mật khẩu",
                "Mã nhóm quyền", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, { "HOAT_DONG", "NGUNG" } },
                new boolean[] { true, false, false, false, false });
    }
}