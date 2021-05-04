package com.example.demo.basic.file;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/file")
public class FileController {

    @GetMapping
    public String fileUploadForm(){
        return "files/index";
    }

    @PostMapping
    public String fileUpload(@RequestParam MultipartFile file,
                             RedirectAttributes attributes){
        // save
        String message = file.getOriginalFilename() + " is Uploaded";
        attributes.addFlashAttribute("message", message);
        return "redirect:/file";
    }
}
