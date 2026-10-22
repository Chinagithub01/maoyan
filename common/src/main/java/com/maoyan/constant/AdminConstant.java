package com.maoyan.constant;

public final class AdminConstant {
    private AdminConstant() {
    }

    public static final String INITIAL_USERNAME = "admin";
    public static final String INITIAL_PASSWORD = "123456";
    public static final String INITIAL_NICKNAME = "系统管理员";
    public static final int ENABLED = 1;
    public static final int TOKEN_HOURS = 12;
    public static final String LOGIN_FAILED = "用户名或密码不正确";
    public static final String DISABLED = "账号已停用";
    public static final String LOGIN_REQUIRED = "请先登录";
    public static final int POSTER_MAX_BYTES = 5 * 1024 * 1024;
    public static final String POSTER_REQUIRED = "请上传海报图片";
    public static final String POSTER_TYPE = "海报只支持 jpg、png、webp、gif";
    public static final String POSTER_TOO_LARGE = "海报不能超过 5MB";
    public static final String POSTER_EXPIRED = "海报文件已失效，请重新上传";
    public static final String POSTER_OSS_FAILED = "海报上传到阿里云失败";
}
