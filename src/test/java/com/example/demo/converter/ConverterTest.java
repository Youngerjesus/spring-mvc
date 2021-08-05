package com.example.demo.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest {

    @Test
    void stringToInteger(){
        //given
        StringToIntegerConverter converter = new StringToIntegerConverter();
        //when
        Integer result = converter.convert("10");
        //then
        assertEquals(result, 10);
    }

    @Test
    void integerToString(){
        //given
        IntegerToStringConverter converter = new IntegerToStringConverter();
        //when
        String result = converter.convert(10);
        //then
        assertEquals(result, "10");
    }
}
