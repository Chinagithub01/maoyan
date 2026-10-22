package com.maoyan.constant;

public final class ShowConstant {
    private ShowConstant() {
    }

    public static final int NAME_MAX = 100;
    public static final int VENUE_MAX = 100;
    public static final int ARTIST_MAX = 100;
    public static final int POSTER_MAX = 255;
    public static final int ADDRESS_MAX = 200;
    public static final int PICKUP_MAX = 20;
    public static final int NAME_MIN = 1;
    public static final int MAX_PER_ORDER_MIN = 1;
    public static final int MAX_PER_ORDER_MAX = 6;
    public static final int DEFAULT_MAX_PER_ORDER = 6;
    public static final int HOT_LIMIT = 10;
    public static final int FLAG_NO = 0;
    public static final int FLAG_YES = 1;
    public static final String DEFAULT_PICKUP_TYPE = "电子票";

    public static final String REQUIRED = "请填写演出信息";
    public static final String NAME_LENGTH = "演出名称需要 " + NAME_MIN + " 到 " + NAME_MAX + " 个字符";
    public static final String TYPE_REQUIRED = "请选择演出类型";
    public static final String CITY_REQUIRED = "请选择城市";
    public static final String VENUE_LENGTH = "场馆名称需要 " + NAME_MIN + " 到 " + VENUE_MAX + " 个字符";
    public static final String ARTIST_LENGTH = "艺人名称不能超过 " + ARTIST_MAX + " 个字符";
    public static final String POSTER_LENGTH = "海报地址不能超过 " + POSTER_MAX + " 个字符";
    public static final String ADDRESS_LENGTH = "地址不能超过 " + ADDRESS_MAX + " 个字符";
    public static final String DURATION_INVALID = "时长需要大于 0 分钟";
    public static final String SEAT_LABEL = "是否可选座";
    public static final String REFUND_LABEL = "是否可退";
    public static final String STOPPED_LABEL = "是否停售";
    public static final String FLAG_INVALID = "只能是 " + FLAG_NO + " 或 " + FLAG_YES;
    public static final String PICKUP_LENGTH = "取票方式不能超过 " + PICKUP_MAX + " 个字符";
    public static final String MAX_PER_ORDER_RANGE = "每笔订单限购张数需要在 " + MAX_PER_ORDER_MIN + " 到 " + MAX_PER_ORDER_MAX + " 之间";
    public static final String SALE_TIME_ORDER = "停售时间需要晚于开售时间";
    public static final String NOT_FOUND = "演出不存在";
    public static final String REFERENCE_MISSING = "演出类型或城市不存在";
    public static final String HAS_SESSION_OR_TICKET = "该演出下已有场次或票，不能删除";
    public static final String CITY_NOT_FOUND = "城市不存在";
}
