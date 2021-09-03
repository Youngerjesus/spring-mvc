package com.example.demo.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void findHello(){
        //given
        String result = ms.getMessage("hello", null, null);
        //when
        //then
        assertEquals(result, "hi");
    }

    @Test
    void noFoundMessage(){
        //given
        //when
        //then
        assertThrows(NoSuchMessageException.class, () -> {
            String result = ms.getMessage("no_code", null, null);
        });
    }

    @Test
    void noFoundMessageButDefaultMessage(){
        //given
        String result = ms.getMessage("no_code", null, "기본 메시지", null);
        //when
        //then
        assertEquals(result, "기본 메시지");
    }
}
