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

@SpringBootTest(classes = UserContextResolverTest.TestApp.class)
@AutoConfigureMockMvc
class UserContextResolverTest {

    @SpringBootApplication
    static class TestApp {

        @RestController
        static class ResolverController {

            @GetMapping("/resolve")
            public String resolve(UserContext userContext) {
                return userContext.userId() + "|"
                        + String.join(",", userContext.roles()) + "|"
                        + userContext.email();
            }
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void resolvesUserContextFromHeaders() throws Exception {
        mockMvc.perform(get("/resolve")
                        .header("X-User-Id", "user-42")
                        .header("X-User-Roles", "courier")
                        .header("X-User-Email", "courier@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("user-42|courier|courier@example.com"));
    }

    @Test
    void missingUserIdReturns401() throws Exception {
        mockMvc.perform(get("/resolve"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void emptyEmailResolvedAsNull() throws Exception {
        mockMvc.perform(get("/resolve")
                        .header("X-User-Id", "user-99")
                        .header("X-User-Roles", "customer")
                        .header("X-User-Email", ""))
                .andExpect(status().isOk())
                .andExpect(content().string("user-99|customer|null"));
    }
}
