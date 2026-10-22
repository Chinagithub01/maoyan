package com.maoyan.vo;

import com.maoyan.entity.ShowType;

public class ShowTypeVO {
    private Long id;
    private String name;

    public static ShowTypeVO of(ShowType showType) {
        ShowTypeVO vo = new ShowTypeVO();
        vo.id = showType.getId();
        vo.name = showType.getName();
        return vo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
