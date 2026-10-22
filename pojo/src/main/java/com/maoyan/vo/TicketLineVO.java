package com.maoyan.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketLineVO {
    private Long id;
    private String orderNo;
    private Long showId;
    private String showName;
    private String venueName;
    private LocalDateTime showTime;
    private String priceTierName;
    private Long seatId;
    private String seatNo;
    private BigDecimal price;
    private Integer status;
    private Integer refundEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
