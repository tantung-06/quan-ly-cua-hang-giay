package security;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import dto.TaiKhoan;

import static security.PermissionAction.*;
import static security.PermissionModule.*;

public final class AccessPolicy {
    public static final String ADMIN_ROLE = "NQ001";
    private final TaiKhoan account;

    public AccessPolicy(TaiKhoan account) {
        this.account = account;
    }

    public boolean can(PermissionModule module, PermissionAction action) {
        if (account == null || !"HOAT_DONG".equals(account.getTrangThai()) || !module.supports(action)) {
            return false;
        }
        try {
            Set<String> permissions = effectivePermissions(account.getMaNhomQuyen(), account.getQuyenHan());
            return permissions.contains(module.key(VIEW)) && permissions.contains(module.key(action));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean canOpen(String label) {
        if (account == null || !"HOAT_DONG".equals(account.getTrangThai())) return false;
        if ("Trang chủ".equals(label)) return true;
        try {
            return can(PermissionModule.fromLabel(label), VIEW);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void require(PermissionModule module, PermissionAction action) {
        if (!can(module, action)) {
            throw new IllegalArgumentException("Bạn không có quyền " + action.label().toLowerCase()
                    + " trong chức năng " + module.label() + ".");
        }
    }

    public static Set<String> effectivePermissions(String role, String stored) {
        if (ADMIN_ROLE.equals(role)) return defaults(role);
        return stored == null ? defaults(role) : decode(stored);
    }

    public static Set<String> defaults(String role) {
        Set<String> result = new LinkedHashSet<>();
        if (ADMIN_ROLE.equals(role)) {
            for (PermissionModule module : PermissionModule.values()) grant(result, module);
        } else if ("NQ002".equals(role)) {
            for (PermissionModule module : new PermissionModule[] { PRODUCT, ATTRIBUTE, PURCHASE, SUPPLIER }) {
                grant(result, module);
            }
        } else if ("NQ003".equals(role)) {
            grant(result, PRODUCT, VIEW);
            grant(result, ATTRIBUTE, VIEW);
            grant(result, SALE);
            grant(result, CUSTOMER, VIEW, CREATE, EDIT);
            grant(result, PROMOTION, VIEW);
        }
        return result;
    }

    private static void grant(Set<String> result, PermissionModule module, PermissionAction... actions) {
        PermissionAction[] granted = actions.length == 0 ? PermissionAction.values() : actions;
        for (PermissionAction action : granted) {
            if (module.supports(action)) result.add(module.key(action));
        }
    }

    public static Set<String> decode(String stored) {
        Set<String> result = new LinkedHashSet<>();
        if (stored == null || stored.isBlank()) return result;
        for (String key : stored.split(",")) {
            String[] parts = key.trim().split(":", -1);
            try {
                if (parts.length != 2) throw new IllegalArgumentException();
                PermissionModule module = PermissionModule.valueOf(parts[0]);
                PermissionAction action = PermissionAction.valueOf(parts[1]);
                if (!module.supports(action)) throw new IllegalArgumentException();
                result.add(module.key(action));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Cấu hình quyền không hợp lệ: " + key);
            }
        }
        for (PermissionModule module : PermissionModule.values()) {
            boolean hasAction = result.stream().anyMatch(key -> key.startsWith(module.name() + ":"));
            if (hasAction && !result.contains(module.key(VIEW))) {
                throw new IllegalArgumentException("Cần cấp quyền Xem cho chức năng " + module.label() + ".");
            }
        }
        return result;
    }

    public static String encode(Set<String> permissions) {
        return decode(String.join(",", permissions)).stream().sorted().collect(Collectors.joining(","));
    }
}
