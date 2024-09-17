package com.example.apigatewayservice.config;

import com.example.apigatewayservice.interceptor.TenantIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public TenantIdInterceptor tenantIdInterceptor() {
        return new TenantIdInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // apply the interceptor all paths
        registry.addInterceptor(tenantIdInterceptor()).addPathPatterns("/**");
    }
}

