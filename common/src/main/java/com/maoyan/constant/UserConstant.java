package com.maoyan.constant;

public final class UserConstant {
    private UserConstant() {
    }

    public static final int USERNAME_MIN = 2;
    public static final int USERNAME_MAX = 20;
    public static final int PASSWORD_MIN = 6;
    public static final int PHONE_LENGTH = 11;
    public static final String PHONE_PATTERN = "1\\d{10}";

    public static final String USERNAME_LENGTH = "用户名需要 " + USERNAME_MIN + " 到 " + USERNAME_MAX + " 个字符";
    public static final String PASSWORD_TOO_SHORT = "密码至少 " + PASSWORD_MIN + " 位";
    public static final String PHONE_INVALID = "请输入 " + PHONE_LENGTH + " 位手机号";
    public static final String LOGIN_REQUIRED = "请输入用户名和密码";
    public static final String USERNAME_EXISTS = "用户名已存在";
    public static final String PHONE_REGISTERED = "手机号已注册";
    public static final String ACCOUNT_REGISTERED = "用户名或手机号已注册";
    public static final String LOGIN_FAILED = "用户名或密码不正确";
}
