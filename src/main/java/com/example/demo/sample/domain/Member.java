package com.example.demo.sample.domain;


public class Member {
    private Long id;
    private String nickname;
    private String role;

    public Member() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
