package com.example.medicalrecord.web.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public String getIndex(Model model) {
        final String welcomeMessage = "Welcome to our Medical Record!";
        model.addAttribute("welcome", welcomeMessage);
        return "index";
    }

    @GetMapping("/unauthorized")
    public String getUnauthorized(Model model) {
        model.addAttribute("message", "You are not authorized to be here!");
        return "/errors/unauthorized-errors";
    }

}
