package com.dls.authlib;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AutoConfiguration
public class AuthLibAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public RbacInterceptor rbacInterceptor() {
        return new RbacInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rbacInterceptor())
                .excludePathPatterns("/health", "/actuator/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserContextResolver());
    }
}
