package com.example.demo.basic.databinder;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataBinderController {
    @InitBinder
    public void initDataBinder(WebDataBinder webDataBinder){
        webDataBinder.setDisallowedFields("id");
    }
}
