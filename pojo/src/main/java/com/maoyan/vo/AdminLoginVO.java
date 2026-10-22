package com.maoyan.vo;

public class AdminLoginVO {
    private String token;
    private Long id;
    private String username;
    private String nickname;

    public AdminLoginVO(String token, Long id, String username, String nickname) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
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
}
