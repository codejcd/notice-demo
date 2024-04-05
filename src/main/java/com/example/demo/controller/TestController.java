package com.example.demo.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Hello Controller
 * 테스트 용
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {

        return "hello";
    }
}
