package com.maoyan.controller.user;

import com.maoyan.constant.UserConstant;
import com.maoyan.dto.UserDTO;
import com.maoyan.result.Result;
import com.maoyan.service.UserService;
import com.maoyan.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserDTO form) {
        String username = text(form.getUsername());
        String password = form.getPassword() == null ? "" : form.getPassword();
        String phone = text(form.getPhone());
        String nickname = text(form.getNickname());
        if (nickname.isEmpty()) {
            nickname = username;
        }
        if (username.length() < UserConstant.USERNAME_MIN || username.length() > UserConstant.USERNAME_MAX) {
            return Result.error(UserConstant.USERNAME_LENGTH);
        }
        if (password.length() < UserConstant.PASSWORD_MIN) {
            return Result.error(UserConstant.PASSWORD_TOO_SHORT);
        }
        if (!phone.matches(UserConstant.PHONE_PATTERN)) {
            return Result.error(UserConstant.PHONE_INVALID);
        }
        try {
            return Result.success(userService.register(username, password, phone, nickname));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO form) {
        String username = text(form.getUsername());
        String password = form.getPassword() == null ? "" : form.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            return Result.error(UserConstant.LOGIN_REQUIRED);
        }
        try {
            return Result.success(userService.login(username, password));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
