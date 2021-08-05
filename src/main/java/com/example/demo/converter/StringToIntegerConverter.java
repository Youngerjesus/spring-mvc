package com.example.demo.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String s) {
        log.info("converter source = " + s);
        return Integer.valueOf(s);
    }
}
