package com.maoyan.admin.controller;

import com.maoyan.admin.service.AdminAccountService;
import com.maoyan.constant.AdminConstant;
import com.maoyan.dto.UserDTO;
import com.maoyan.result.Result;
import com.maoyan.vo.AdminLoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminLoginController {
    private final AdminAccountService adminAccountService;

    public AdminLoginController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody UserDTO form) {
        String username = form == null || form.getUsername() == null ? "" : form.getUsername().trim();
        String password = form == null || form.getPassword() == null ? "" : form.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            return Result.error(AdminConstant.LOGIN_FAILED);
        }
        try {
            return Result.success(adminAccountService.login(username, password));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }
}
