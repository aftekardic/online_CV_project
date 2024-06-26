package com.app.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.business.dto.LoginRequestDto;
import com.app.backend.business.dto.RegisterRequestDto;
import com.app.backend.business.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/sign-in")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        return authService.login(request, servletRequest, servletResponse);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDto request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        return authService.register(request, servletRequest, servletResponse);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<Object> refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return authService.refreshToken(servletRequest, servletResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletRequest servletRequest) {
        return authService.logout(request, servletRequest);
    }
}
