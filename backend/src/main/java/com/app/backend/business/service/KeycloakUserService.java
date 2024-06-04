package com.app.backend.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.backend.business.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class KeycloakUserService {
    @Value("${keycloak.userinfo-url}")
    private String kcGetUserinfoUrl;

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
}
