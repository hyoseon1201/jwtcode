package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // 테스트용 엔드포인트 입니다. 해당 url에 접근하려면 User권한을 가진 jwt 토큰이 필요합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/test")
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "현재 접속중인 유저는 : " + username + " 입니다.";
    }
}
