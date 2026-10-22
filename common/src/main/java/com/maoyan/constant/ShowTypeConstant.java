package com.maoyan.constant;

public final class ShowTypeConstant {
    private ShowTypeConstant() {
    }

    public static final int NAME_MIN = 1;
    public static final int NAME_MAX = 50;

    public static final String NAME_LENGTH = "演出类型名称需要 " + NAME_MIN + " 到 " + NAME_MAX + " 个字符";
    public static final String NOT_FOUND = "演出类型不存在";
    public static final String NAME_EXISTS = "演出类型名称已存在";
    public static final String HAS_SHOW = "该类型下已有演出，不能删除";
}
