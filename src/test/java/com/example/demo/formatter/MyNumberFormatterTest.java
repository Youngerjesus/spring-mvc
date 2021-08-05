package com.example.demo.formatter;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MyNumberFormatterTest {

    @Test
    void parse() throws ParseException {
        //given
        MyNumberFormatter formatter = new MyNumberFormatter();
        //when
        Number result = formatter.parse("1,000", Locale.KOREA);
        //then
        assertEquals(result, 1000L);
    }

    @Test
    void print(){
        //given
        MyNumberFormatter formatter = new MyNumberFormatter();
        //then
        String print = formatter.print(1000, Locale.KOREA);
        System.out.println(print);
    }

}