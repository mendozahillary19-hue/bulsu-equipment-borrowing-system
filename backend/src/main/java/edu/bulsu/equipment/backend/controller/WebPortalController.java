package edu.bulsu.equipment.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPortalController {

    @GetMapping(value = {"/", "/portal", "/api"})
    public String index() {
        return "forward:/index.html";
    }
}
