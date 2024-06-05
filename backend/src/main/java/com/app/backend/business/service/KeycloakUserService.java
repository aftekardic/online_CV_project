package com.app.backend.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.backend.business.dto.FormUserDto;
import com.app.backend.business.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class KeycloakUserService {
        @Value("${keycloak.userinfo-url}")
        private String kcGetUserinfoUrl;

        @Value("${keycloak.update-url}")
        private String kcUpdateUrl;

        @Autowired
        private RestTemplate restTemplate;

        private static final String AUTHORIZATION = "Authorization";

        public UserDto getUserInformationsByEmail(HttpServletRequest servletRequest) {
                String authorization = servletRequest.getHeader(AUTHORIZATION);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set(AUTHORIZATION, authorization);

                ResponseEntity<UserDto> response = restTemplate.postForEntity(kcGetUserinfoUrl,
                                new HttpEntity<>(headers), UserDto.class);
                return response.getBody();
        }

        public ResponseEntity<?> updateUserInformations(FormUserDto formUserDto,
                        HttpServletRequest servletRequest) {
                String authorization = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set(HttpHeaders.AUTHORIZATION, authorization);

                try {
                        if (formUserDto.getPassword() != null) {

                                String dynamicJsonForPassword = String.format(
                                                "{\"type\":\"password\",\"temporary\":false,\"value\":\"%s\"}",
                                                formUserDto.getPassword());

                                restTemplate.exchange(
                                                kcUpdateUrl + "/" + formUserDto.getSub() + "/reset-password",
                                                HttpMethod.PUT,
                                                new HttpEntity<>(dynamicJsonForPassword,
                                                                headers),
                                                Object.class);
                        }

                        String dynamicJsonForUserInfo = String.format(
                                        "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\"}",
                                        formUserDto.getGiven_name(), formUserDto.getFamily_name(),
                                        formUserDto.getEmail());

                        restTemplate.exchange(
                                        kcUpdateUrl + "/" + formUserDto.getSub(),
                                        HttpMethod.PUT,
                                        new HttpEntity<>(dynamicJsonForUserInfo,
                                                        headers),
                                        Object.class);

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("There is an error when updating...");
                }

                return ResponseEntity.status(HttpStatus.OK).body("User updates successfully...");
        }

}
