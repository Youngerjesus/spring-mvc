package com.example.demo.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegerTest {

    @Test
    void parseIntVsValueOf(){
        //given
        String text = "99999";
        //when
        Integer number = Integer.valueOf(text);
        int number2 = Integer.parseInt(text);
        //then
        assertEquals(number, 99999);
        assertEquals(number2, 99999);
    }
}
