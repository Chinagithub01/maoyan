package com.maoyan.vo;

public class ActivityTypeVO {
    private Long id;
    private String name;

    public ActivityTypeVO(Long id, String name) {
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
