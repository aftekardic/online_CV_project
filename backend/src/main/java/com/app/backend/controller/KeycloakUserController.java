package com.app.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/user")
public class KeycloakUserController {

    @GetMapping("/info")
    public ResponseEntity<?> getUserInformationsByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body("okke");
    }
}
