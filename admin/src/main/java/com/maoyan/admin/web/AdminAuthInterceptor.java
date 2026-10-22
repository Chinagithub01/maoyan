package com.maoyan.admin.web;

import com.maoyan.admin.service.AdminAccountService;
import com.maoyan.constant.AdminConstant;
import com.maoyan.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    private final AdminAccountService adminAccountService;
    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(AdminAccountService adminAccountService, ObjectMapper objectMapper) {
        this.adminAccountService = adminAccountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : "";
        try {
            request.setAttribute("admin", adminAccountService.authenticate(token));
            return true;
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), Result.error(
                    exception.getMessage() == null ? AdminConstant.LOGIN_REQUIRED : exception.getMessage()));
            return false;
        }
    }
}
