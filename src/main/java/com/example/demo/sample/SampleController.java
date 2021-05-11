package com.example.demo.sample;

import com.example.demo.sample.domain.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") Person person){
        return "hello " + person.getName();
    }
}
