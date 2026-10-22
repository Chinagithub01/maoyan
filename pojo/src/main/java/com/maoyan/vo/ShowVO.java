package com.maoyan.vo;

import com.maoyan.entity.Show;

import java.time.LocalDateTime;

public class ShowVO {
    private Long id;
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

    public static ShowVO of(Show show) {
        ShowVO vo = new ShowVO();
        vo.id = show.getId();
        vo.showTypeId = show.getShowTypeId();
        vo.cityId = show.getCityId();
        vo.name = show.getName();
        vo.artist = show.getArtist();
        vo.poster = show.getPoster();
        vo.duration = show.getDuration();
        vo.intro = show.getIntro();
        vo.notice = show.getNotice();
        vo.venueName = show.getVenueName();
        vo.address = show.getAddress();
        vo.seatEnabled = show.getSeatEnabled();
        vo.refundEnabled = show.getRefundEnabled();
        vo.pickupType = show.getPickupType();
        vo.maxPerOrder = show.getMaxPerOrder();
        vo.saleStartTime = show.getSaleStartTime();
        vo.saleEndTime = show.getSaleEndTime();
        vo.stopped = show.getStopped();
        return vo;
    }

    public Long getId() {
        return id;
    }

    public Long getShowTypeId() {
        return showTypeId;
    }

    public Long getCityId() {
        return cityId;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getPoster() {
        return poster;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getIntro() {
        return intro;
    }

    public String getNotice() {
        return notice;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getAddress() {
        return address;
    }

    public Integer getSeatEnabled() {
        return seatEnabled;
    }

    public Integer getRefundEnabled() {
        return refundEnabled;
    }

    public String getPickupType() {
        return pickupType;
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public LocalDateTime getSaleEndTime() {
        return saleEndTime;
    }

    public Integer getStopped() {
        return stopped;
    }
}
