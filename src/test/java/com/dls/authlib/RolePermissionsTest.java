package com.dls.authlib;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionsTest {

    @Test
    void customerHasOrdersCreate() {
        assertTrue(RolePermissions.hasPermission(List.of("customer"), Permission.ORDERS_CREATE));
    }

    @Test
    void customerLacksCouriersUpdate() {
        assertFalse(RolePermissions.hasPermission(List.of("customer"), Permission.COURIERS_UPDATE));
    }

    @Test
    void adminWildcardGrantsAnyPermission() {
        assertTrue(RolePermissions.hasPermission(List.of("admin"), "nonexistent_permission"));
    }

    @Test
    void unknownRoleReturnsFalse() {
        assertFalse(RolePermissions.hasPermission(List.of("unknown_role"), Permission.ORDERS_CREATE));
    }

    @Test
    void emptyRolesReturnsFalse() {
        assertFalse(RolePermissions.hasPermission(List.of(), Permission.ORDERS_CREATE));
    }

    @Test
    void multipleRolesCombine() {
        assertTrue(RolePermissions.hasPermission(
                List.of("customer", "courier"), Permission.DELIVERIES_UPDATE));
    }

    @Test
    void restaurantHasAllMenuPermissions() {
        assertTrue(RolePermissions.hasPermission(List.of("restaurant"), Permission.MENU_READ));
        assertTrue(RolePermissions.hasPermission(List.of("restaurant"), Permission.MENU_CREATE));
        assertTrue(RolePermissions.hasPermission(List.of("restaurant"), Permission.MENU_UPDATE));
        assertTrue(RolePermissions.hasPermission(List.of("restaurant"), Permission.MENU_DELETE));
    }

    @Test
    void courierHasDeliveriesUpdate() {
        assertTrue(RolePermissions.hasPermission(List.of("courier"), Permission.DELIVERIES_UPDATE));
    }

    @Test
    void mapContainsExactlyFourRoles() {
        assertEquals(4, RolePermissions.ROLE_PERMISSIONS.size());
        assertTrue(RolePermissions.ROLE_PERMISSIONS.containsKey("customer"));
        assertTrue(RolePermissions.ROLE_PERMISSIONS.containsKey("courier"));
        assertTrue(RolePermissions.ROLE_PERMISSIONS.containsKey("restaurant"));
        assertTrue(RolePermissions.ROLE_PERMISSIONS.containsKey("admin"));
    }

    @Test
    void adminSetContainsOnlyWildcard() {
        assertEquals(Set.of("*"), RolePermissions.ROLE_PERMISSIONS.get("admin"));
    }

    @Test
    void customerHasSevenPermissions() {
        assertEquals(7, RolePermissions.ROLE_PERMISSIONS.get("customer").size());
    }

    @Test
    void courierHasEightPermissions() {
        assertEquals(8, RolePermissions.ROLE_PERMISSIONS.get("courier").size());
    }

    @Test
    void restaurantHasElevenPermissions() {
        assertEquals(11, RolePermissions.ROLE_PERMISSIONS.get("restaurant").size());
    }
}
