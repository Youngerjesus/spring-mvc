package com.example.demo.basic.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionController {

    @ExceptionHandler
    public String eventErrorHandler(EventException exception, Model model){
        model.addAttribute("message", "event error");
        return "error";
    }

    @ExceptionHandler({EventException2.class, RuntimeException.class})
    public String eventErrorsHandler(RuntimeException exception, Model model){
        model.addAttribute("message", "event error");
        return "error";
    }

    @GetMapping("/createError")
    public String createError(){
        throw new EventException();
    }
}
