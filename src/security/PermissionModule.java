package security;

import java.util.Set;

import static security.PermissionAction.*;

public enum PermissionModule {
    PRODUCT("Sản phẩm", VIEW, CREATE, EDIT, DELETE),
    ATTRIBUTE("Thuộc tính", VIEW, CREATE, EDIT, DELETE),
    PURCHASE("Phiếu nhập", VIEW, CREATE, COMPLETE, CANCEL),
    SALE("Hóa đơn", VIEW, CREATE, COMPLETE, CANCEL),
    SUPPLIER("Nhà cung cấp", VIEW, CREATE, EDIT, DELETE),
    CUSTOMER("Khách hàng", VIEW, CREATE, EDIT, DELETE),
    EMPLOYEE("Nhân viên", VIEW, CREATE, EDIT, DELETE),
    ACCOUNT("Tài khoản", VIEW, CREATE, EDIT, DELETE),
    ROLE("Phân quyền", VIEW, CREATE, EDIT, DELETE),
    PROMOTION("Khuyến mãi", VIEW, CREATE, EDIT, DELETE),
    STATISTICS("Thống kê", VIEW);

    private final String label;
    private final Set<PermissionAction> actions;

    PermissionModule(String label, PermissionAction... actions) {
        this.label = label;
        this.actions = Set.of(actions);
    }

    public String label() {
        return label;
    }

    public boolean supports(PermissionAction action) {
        return actions.contains(action);
    }

    public String key(PermissionAction action) {
        return name() + ":" + action.name();
    }

    public static PermissionModule fromLabel(String label) {
        for (PermissionModule module : values()) {
            if (module.label.equals(label)) return module;
        }
        throw new IllegalArgumentException("Chức năng không được hỗ trợ: " + label);
    }
}
