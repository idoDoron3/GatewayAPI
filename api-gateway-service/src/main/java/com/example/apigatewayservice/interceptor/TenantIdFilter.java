package com.example.apigatewayservice.interceptor;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TenantIdFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast the ServletRequest and ServletResponse to HTTP equivalents
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String tenantId = httpRequest.getHeader("tenantId");

        if (tenantId != null && !tenantId.isEmpty()) {
            // Log the tenantId if present
            logger.info("Got an HTTP request for {}", tenantId);
        } else {
            // Log that no tenantId was found
            logger.info("No tenantId found in the request headers.");
        }

        // Continue with the next filter or the controller
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
