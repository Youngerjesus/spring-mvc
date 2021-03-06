package com.example.demo.basic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelloData {
    private String username;
    private int age;

    public HelloData(String username, int age) {
        this.username = username;
        this.age = age;
    }

    @Override
    public String toString() {
        return "HelloData{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }

    public HelloData() {
    }
}
