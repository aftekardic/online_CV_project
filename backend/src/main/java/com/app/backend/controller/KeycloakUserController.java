package com.app.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.business.dto.FormUserDto;
import com.app.backend.business.dto.UserDto;
import com.app.backend.business.service.KeycloakUserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/user")
public class KeycloakUserController {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInformationsByEmail(HttpServletRequest servletRequest) {
        UserDto user = keycloakUserService.getUserInformationsByEmail(servletRequest);
        return ResponseEntity.ok().body(UserDto.builder()
                .status("SUCCESS")
                .sub(user.getSub())
                .email_verified(user.getEmail_verified())
                .name(user.getName())
                .preferred_username(user.getPreferred_username())
                .given_name(user.getGiven_name())
                .family_name(user.getFamily_name())
                .email(user.getEmail())
                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserInformations(@RequestBody FormUserDto formUserDto,
            HttpServletRequest servletRequest) {
        return keycloakUserService.updateUserInformations(formUserDto, servletRequest);
    }
}
