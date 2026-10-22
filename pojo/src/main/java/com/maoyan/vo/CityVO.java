package com.maoyan.vo;

public class CityVO {
    private Long id;
    private String name;

    public CityVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
