package com.localproblemsolver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemController {

    @GetMapping("/api/problems/test")
    public String test() {
        return "Problem Controller is working!";
    }
}