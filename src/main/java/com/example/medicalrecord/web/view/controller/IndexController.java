package com.example.medicalrecord.web.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


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
    public String getUnauthorized(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "errorMessage", required = false) String errorMessage,
            Model model) {

        model.addAttribute("from", from);
        model.addAttribute("message", errorMessage != null && !errorMessage.isBlank()
                ? errorMessage
                : "You are not authorized to be here!");
        return "unauthorized";
    }
}