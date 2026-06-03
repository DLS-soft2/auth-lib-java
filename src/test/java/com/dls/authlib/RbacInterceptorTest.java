package com.dls.authlib;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RbacInterceptorTest.TestApp.class)
@AutoConfigureMockMvc
class RbacInterceptorTest {

    @SpringBootApplication
    static class TestApp {

        @RestController
        static class TestController {

            @GetMapping("/protected")
            @RequirePermission(Permission.RESTAURANTS_READ)
            public String protectedEndpoint() {
                return "ok";
            }

            @GetMapping("/admin-only")
            @RequirePermission(Permission.RESTAURANTS_CREATE)
            public String adminOnly() {
                return "admin";
            }

            @GetMapping("/open")
            public String openEndpoint() {
                return "open";
            }

            @GetMapping("/context")
            @RequirePermission(Permission.ORDERS_CREATE)
            public String contextEndpoint(UserContext userContext) {
                return userContext.userId() + "|"
                        + String.join(",", userContext.roles()) + "|"
                        + userContext.email();
            }
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void protectedEndpointWithNoHeadersReturns401() throws Exception {
        mockMvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Missing user context\"}"));
    }

    @Test
    void protectedEndpointWithEmptyUserIdReturns401() throws Exception {
        mockMvc.perform(get("/protected")
                        .header("X-User-Id", "")
                        .header("X-User-Roles", "customer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithWrongRoleReturns403() throws Exception {
        mockMvc.perform(get("/admin-only")
                        .header("X-User-Id", "user-1")
                        .header("X-User-Roles", "courier"))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"error\":\"Insufficient permissions\"}"));
    }

    @Test
    void protectedEndpointWithCorrectRoleReturns200() throws Exception {
        mockMvc.perform(get("/protected")
                        .header("X-User-Id", "user-1")
                        .header("X-User-Roles", "customer"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void adminWildcardBypassesPermissionCheck() throws Exception {
        mockMvc.perform(get("/admin-only")
                        .header("X-User-Id", "user-1")
                        .header("X-User-Roles", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    void endpointWithoutAnnotationAndValidHeadersReturns200() throws Exception {
        mockMvc.perform(get("/open")
                        .header("X-User-Id", "user-1")
                        .header("X-User-Roles", "customer"))
                .andExpect(status().isOk())
                .andExpect(content().string("open"));
    }

    @Test
    void endpointWithoutAnnotationAndNoHeadersReturns401() throws Exception {
        mockMvc.perform(get("/open"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void multipleRolesOneHasPermissionReturns200() throws Exception {
        mockMvc.perform(get("/protected")
                        .header("X-User-Id", "user-1")
                        .header("X-User-Roles", "courier,customer"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void userContextInjectedCorrectlyInController() throws Exception {
        mockMvc.perform(get("/context")
                        .header("X-User-Id", "user-123")
                        .header("X-User-Roles", "customer,admin")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("user-123|customer,admin|test@example.com"));
    }

    @Test
    void whitespaceInRolesIsStripped() throws Exception {
        mockMvc.perform(get("/context")
                        .header("X-User-Id", "user-123")
                        .header("X-User-Roles", " customer , admin ")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("user-123|customer,admin|test@example.com"));
    }

    @Test
    void emptyEmailBecomesNull() throws Exception {
        mockMvc.perform(get("/context")
                        .header("X-User-Id", "user-123")
                        .header("X-User-Roles", "customer")
                        .header("X-User-Email", ""))
                .andExpect(status().isOk())
                .andExpect(content().string("user-123|customer|null"));
    }
}
