package com.dls.authlib;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {

    public static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "customer", Set.of(
                    Permission.ORDERS_CREATE, Permission.ORDERS_READ,
                    Permission.RESTAURANTS_READ,
                    Permission.PAYMENTS_READ,
                    Permission.USERS_READ, Permission.USERS_UPDATE
            ),
            "courier", Set.of(
                    Permission.COURIERS_READ, Permission.COURIERS_UPDATE,
                    Permission.DELIVERIES_READ, Permission.DELIVERIES_UPDATE,
                    Permission.RESTAURANTS_READ,
                    Permission.USERS_READ, Permission.USERS_UPDATE
            ),
            "restaurant", Set.of(
                    Permission.RESTAURANTS_READ, Permission.RESTAURANTS_CREATE,
                    Permission.RESTAURANTS_UPDATE, Permission.RESTAURANTS_DELETE,
                    Permission.MENU_READ, Permission.MENU_CREATE,
                    Permission.MENU_UPDATE, Permission.MENU_DELETE,
                    Permission.USERS_READ, Permission.USERS_UPDATE
            ),
            "admin", Set.of("*")
    );

    public static boolean hasPermission(List<String> roles, String permission) {
        for (String role : roles) {
            Set<String> perms = ROLE_PERMISSIONS.getOrDefault(role, Set.of());
            if (perms.contains("*") || perms.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    private RolePermissions() {
    }
}
