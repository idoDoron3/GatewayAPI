package com.example.apigatewayservice.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantIdInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TenantIdInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // extract the tenantId header
        String tenantId = request.getHeader("tenantId");
        if (tenantId != null && !tenantId.isBlank()) {
            logger.info("Got an HTTP request for {}", tenantId);
        }
        // allow the request to proceed
        return true;
    }
}
