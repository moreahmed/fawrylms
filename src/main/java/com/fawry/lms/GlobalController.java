package com.fawry.lms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalController {

    @GetMapping("/health")
    public String health() {
        return "Fawry is up";
    }    
}
