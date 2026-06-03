package com.dls.authlib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

public class RbacInterceptor implements HandlerInterceptor {

    static final String USER_CONTEXT_ATTRIBUTE = "userContext";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing user context");
            return false;
        }

        String rawRoles = request.getHeader("X-User-Roles");
        List<String> roles = parseRoles(rawRoles);

        String rawEmail = request.getHeader("X-User-Email");
        String email = (rawEmail != null && !rawEmail.isEmpty()) ? rawEmail : null;

        UserContext userContext = new UserContext(userId, roles, email);
        request.setAttribute(USER_CONTEXT_ATTRIBUTE, userContext);

        RequirePermission annotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (annotation != null && !RolePermissions.hasPermission(roles, annotation.value())) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
            return false;
        }

        return true;
    }

    private static List<String> parseRoles(String rawRoles) {
        if (rawRoles == null || rawRoles.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(rawRoles.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static void writeJsonError(HttpServletResponse response, int status,
                                       String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
