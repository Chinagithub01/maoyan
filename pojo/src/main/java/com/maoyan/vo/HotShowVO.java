package com.maoyan.vo;

public class HotShowVO {
    private Long id;
    private String name;
    private String artist;
    private String poster;
    private String venueName;
    private Long cityId;
    private String cityName;
    private Long showTypeId;
    private String showTypeName;
    private Long soldCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getShowTypeId() {
        return showTypeId;
    }

    public void setShowTypeId(Long showTypeId) {
        this.showTypeId = showTypeId;
    }

    public String getShowTypeName() {
        return showTypeName;
    }

    public void setShowTypeName(String showTypeName) {
        this.showTypeName = showTypeName;
    }

    public Long getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Long soldCount) {
        this.soldCount = soldCount;
    }
}
