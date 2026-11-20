package gui.dialog;

public final class TaiKhoanDialog extends FormDialog {
    public TaiKhoanDialog(String[] values, boolean editing) {
        this(values, editing, null);
    }

    public TaiKhoanDialog(String[] values, boolean editing, String[] roles) {
        super(new String[] { "Mã tài khoản", "Tên đăng nhập", "Mật khẩu",
                "Nhóm quyền", "Trạng thái" },
                values,
                new String[][] { null, null, null, roles, { "HOAT_DONG", "NGUNG" } },
                new boolean[] { true, false, false, false, false });
    }
}
