package com.dls.authlib;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;

public class UserContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("No HttpServletRequest available");
        }

        Object existing = request.getAttribute(RbacInterceptor.USER_CONTEXT_ATTRIBUTE);
        if (existing instanceof UserContext userContext) {
            return userContext;
        }

        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing user context");
        }

        String rawRoles = request.getHeader("X-User-Roles");
        List<String> roles = (rawRoles == null || rawRoles.isEmpty())
                ? List.of()
                : Arrays.stream(rawRoles.split(","))
                        .map(String::strip)
                        .filter(s -> !s.isEmpty())
                        .toList();

        String rawEmail = request.getHeader("X-User-Email");
        String email = (rawEmail != null && !rawEmail.isEmpty()) ? rawEmail : null;

        return new UserContext(userId, roles, email);
    }
}
