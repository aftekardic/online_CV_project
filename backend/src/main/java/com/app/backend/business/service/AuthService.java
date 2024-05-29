package com.app.backend.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.app.backend.business.dto.AuthResponseDto;
import com.app.backend.business.dto.LoginRequestDto;
import com.app.backend.business.dto.TokenDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SessionStorage sessionStorage;

    @Value("auth-service-v1")
    private String kcClientId;

    @Value("JXU1y3O21y3rZMZdkDQfgxDSDcs16UQb")
    private String kcClientSecret;

    @Value("http://localhost:8080/realms/online-cv-project-realm/protocol/openid-connect/token")
    private String kcGetTokenUrl;

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private static final String ACCESS_TOKEN = "Access-Token";
    private static final String REFRESH_TOKEN = "Refresh-Token";
    private static final String EXPIRES_IN = "Expires-In";
    private static final String DEVICE_ID = "Device-Id";

    public ResponseEntity<Object> login(LoginRequestDto request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {

        String deviceId = servletRequest.getHeader(DEVICE_ID);

        TokenDto tokenDto = this.getAccessToken(request);
        servletResponse.addHeader(ACCESS_TOKEN, tokenDto.getAccess_token());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(tokenDto.getExpires_in()));

        sessionStorage.putCache(REFRESH_TOKEN, deviceId, tokenDto.getRefresh_token(), 1800);

        return ResponseEntity.ok().body(AuthResponseDto.builder()
                .status("SUCCESS")
                .message("Login successfully...")
                .build());
    }

    public ResponseEntity<Object> refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String deviceId = servletRequest.getHeader(DEVICE_ID);
        String refreshToken = (String) sessionStorage.getCache(REFRESH_TOKEN, deviceId);

        TokenDto tokenDto = this.getRefreshToken(refreshToken);

        servletResponse.addHeader(ACCESS_TOKEN, tokenDto.getAccess_token());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(tokenDto.getExpires_in()));

        sessionStorage.putCache(REFRESH_TOKEN, deviceId, tokenDto.getRefresh_token(), tokenDto.getRefresh_expires_in());

        return ResponseEntity.ok().body(AuthResponseDto.builder()
                .status("SUCCESS")
                .build());
    }

    private TokenDto getAccessToken(LoginRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE_PASSWORD);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);
        requestBody.add("username", request.getEmail());
        requestBody.add("password", request.getPassword());

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl,
                new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }

    private TokenDto getRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE_REFRESH_TOKEN);
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl,
                new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }
}
