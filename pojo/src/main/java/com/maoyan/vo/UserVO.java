package com.maoyan.vo;

import com.maoyan.entity.User;

public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;

    public static UserVO of(User user) {
        UserVO vo = new UserVO();
        vo.id = user.getId();
        vo.username = user.getUsername();
        vo.nickname = user.getNickname();
        vo.phone = user.getPhone();
        return vo;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }
}
