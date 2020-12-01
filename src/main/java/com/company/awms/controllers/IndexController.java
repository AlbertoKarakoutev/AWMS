package com.company.awms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class IndexController {
    
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        return "index";
    }
}
