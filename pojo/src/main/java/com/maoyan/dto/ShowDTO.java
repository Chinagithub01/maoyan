package com.maoyan.dto;

import java.time.LocalDateTime;

public class ShowDTO {
    private Long showTypeId;
    private Long cityId;
    private String name;
    private String artist;
    private String poster;
    private Integer duration;
    private String intro;
    private String notice;
    private String venueName;
    private String address;
    private Integer seatEnabled;
    private Integer refundEnabled;
    private String pickupType;
    private Integer maxPerOrder;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndTime;
    private Integer stopped;

    public Long getShowTypeId() {
        return showTypeId;
    }

    public void setShowTypeId(Long showTypeId) {
        this.showTypeId = showTypeId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSeatEnabled() {
        return seatEnabled;
    }

    public void setSeatEnabled(Integer seatEnabled) {
        this.seatEnabled = seatEnabled;
    }

    public Integer getRefundEnabled() {
        return refundEnabled;
    }

    public void setRefundEnabled(Integer refundEnabled) {
        this.refundEnabled = refundEnabled;
    }

    public String getPickupType() {
        return pickupType;
    }

    public void setPickupType(String pickupType) {
        this.pickupType = pickupType;
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }

    public void setMaxPerOrder(Integer maxPerOrder) {
        this.maxPerOrder = maxPerOrder;
    }

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(LocalDateTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public LocalDateTime getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(LocalDateTime saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public Integer getStopped() {
        return stopped;
    }

    public void setStopped(Integer stopped) {
        this.stopped = stopped;
    }
}
