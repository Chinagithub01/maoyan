package com.maoyan.constant;

public final class TicketConstant {
    private TicketConstant() {
    }

    public static final int ISSUED = 1;
    public static final int REFUNDED = 2;
    public static final int SEAT_AVAILABLE = 0;
    public static final String LOGIN_REQUIRED = "请先登录";
    public static final String ORDER_NOT_FOUND = "订单不存在";
    public static final String REFUND_DISABLED = "该演出不支持退票";
    public static final String CANNOT_REFUND = "订单不能退票";
}
