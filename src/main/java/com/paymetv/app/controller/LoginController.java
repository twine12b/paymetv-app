package com.paymetv.app.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name="error", required = false) String error) {
        model.addAttribute("error", error);
        return "auth/login";
    }
}
