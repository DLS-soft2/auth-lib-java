package com.dls.authlib;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {

    @Test
    void storesCorrectValues() {
        UserContext ctx = new UserContext("user-1", List.of("customer", "admin"), "test@example.com");
        assertEquals("user-1", ctx.userId());
        assertEquals(List.of("customer", "admin"), ctx.roles());
        assertEquals("test@example.com", ctx.email());
    }

    @Test
    void emptyRolesListWorks() {
        UserContext ctx = new UserContext("user-2", List.of(), "a@b.com");
        assertTrue(ctx.roles().isEmpty());
    }

    @Test
    void nullEmailWorks() {
        UserContext ctx = new UserContext("user-3", List.of("courier"), null);
        assertNull(ctx.email());
    }
}
