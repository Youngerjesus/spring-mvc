package com.example.demo.basic.model;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/model")
public class ModelController {

    @ModelAttribute
    public void subject(Model model){
        model.addAttribute("subject", "seminar");
    }

    @ModelAttribute("category")
    public String category(Model model){
        return "study";
    }

    @GetMapping
    public Model model(Model model){
        return model;
    }
}
