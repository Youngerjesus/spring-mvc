package com.example.demo.basic.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1(){
        ModelAndView modelAndView = new ModelAndView("response/hello")
                                    .addObject("data", "hello!");
        log.info("modelAndView={}", modelAndView.getView());
        return modelAndView;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model){
        model.addAttribute("data", "hello! v2");
        return "response/hello";
    }

    @RequestMapping("/response/hello")
    public void responseViewV3(Model model){
        model.addAttribute("data", "hello v3");
    }

    @RequestMapping("/response-view-test")
    public String responseViewTest(Model model){
        model.addAttribute("data", "hello test");
        return "hello-view-template-test-2";
    }


}
