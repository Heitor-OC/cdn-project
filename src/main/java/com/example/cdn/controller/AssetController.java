package com.example.cdn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssetController {

    @GetMapping("/cdn/hi")
    public String hi() {
        return "Hello World";
    }

}
