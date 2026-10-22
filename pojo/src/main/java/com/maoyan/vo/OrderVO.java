package com.maoyan.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderVO {
    private String orderNo;
    private Long showId;
    private String showName;
    private String venueName;
    private LocalDateTime showTime;
    private String priceTierName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer status;
    private Integer refundEnabled;
    private List<OrderTicketVO> tickets = new ArrayList<>();

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public String getPriceTierName() {
        return priceTierName;
    }

    public void setPriceTierName(String priceTierName) {
        this.priceTierName = priceTierName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRefundEnabled() {
        return refundEnabled;
    }

    public void setRefundEnabled(Integer refundEnabled) {
        this.refundEnabled = refundEnabled;
    }

    public List<OrderTicketVO> getTickets() {
        return tickets;
    }

    public void setTickets(List<OrderTicketVO> tickets) {
        this.tickets = tickets;
    }
}
