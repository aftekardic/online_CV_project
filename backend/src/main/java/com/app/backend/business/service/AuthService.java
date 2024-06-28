package com.app.backend.business.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.app.backend.business.dto.AuthResponseDto;
import com.app.backend.business.dto.LoginRequestDto;
import com.app.backend.business.dto.RegisterRequestDto;
import com.app.backend.business.dto.TokenDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SessionStorage sessionStorage;

    @Value("${keycloak.client-id}")
    private String kcClientId;

    @Value("${keycloak.client-secret}")
    private String kcClientSecret;

    @Value("${keycloak.token-url}")
    private String kcGetTokenUrl;

    @Value("${keycloak.logout-url}")
    private String kcLogoutUrl;

    @Value("${keycloak.revoke-token-url}")
    private String kcRevokeTokenUrl;

    @Value("${keycloak.update-url}")
    private String kcUpdateUrl;

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
                .accessToken(tokenDto.getAccess_token())
                .refreshToken(tokenDto.getRefresh_token())
                .build());
    }

    public ResponseEntity<Object> register(RegisterRequestDto request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String adminAccessToken = getAdminAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + adminAccessToken);

        String dynamicJsonForUserInfo = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"enabled\":true,\"realmRoles\":[\"USER\"]}",
                request.getFirstName(), request.getLastName(),
                request.getEmail());
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    kcUpdateUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(dynamicJsonForUserInfo,
                            headers),
                    Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String newUserID = getUserSub(request.getEmail(), headers);
                String dynamicJsonForPassword = String.format(
                        "{\"type\":\"password\",\"temporary\":false,\"value\":\"%s\"}",
                        request.getPassword());

                restTemplate.exchange(
                        kcUpdateUrl + "/" + newUserID + "/reset-password",
                        HttpMethod.PUT,
                        new HttpEntity<>(dynamicJsonForPassword,
                                headers),
                        Object.class);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist");
        }

        // else {
        //
        // }

        return ResponseEntity.ok().body("User created...");
    }

    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletRequest servletRequest) {

        String deviceId = servletRequest.getHeader(DEVICE_ID);
        String refreshToken = (String) sessionStorage.getCache(REFRESH_TOKEN, deviceId);

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh-Token is missing or invalid");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);
        requestBody.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> logoutResponse = restTemplate.postForEntity(kcLogoutUrl, entity, String.class);

        requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);
        requestBody.add("token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> revokeEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> revokeResponse = restTemplate.postForEntity(kcRevokeTokenUrl, revokeEntity,
                String.class);
        if (logoutResponse.getStatusCode().is2xxSuccessful() && revokeResponse.getStatusCode().is2xxSuccessful()) {
            sessionStorage.removeCache(REFRESH_TOKEN, deviceId);
            return ResponseEntity.ok().body("Logout successful");
        } else {
            return ResponseEntity.status(logoutResponse.getStatusCode()).body("Logout failed");
        }
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
                .accessToken(tokenDto.getAccess_token())
                .refreshToken(tokenDto.getRefresh_token())
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
        requestBody.add("scope", "openid");

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl,
                new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }

    @SuppressWarnings("null")
    public String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE_PASSWORD);
        requestBody.add("client_id", "admin-cli");
        // requestBody.add("client_secret", kcClientSecret);
        requestBody.add("username", "admin");
        requestBody.add("password", "admin");
        requestBody.add("scope", "openid");

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(
                "http://localhost:8080/realms/master/protocol/openid-connect/token",
                new HttpEntity<>(requestBody, headers), TokenDto.class);
        String adminAccessToken = response.getBody().getAccess_token();
        return adminAccessToken;
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

    public String getUserSub(String username, HttpHeaders headers) {
        ResponseEntity<Object> response = restTemplate.exchange(
                kcUpdateUrl,
                HttpMethod.GET,
                new HttpEntity<>(
                        headers),
                Object.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> users = mapper.convertValue(response.getBody(),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            Optional<Map<String, Object>> foundUser = users.stream()
                    .filter(user -> username.equals(user.get("username")))
                    .findFirst();

            if (foundUser.isPresent()) {
                return foundUser.get().get("id").toString();
            }
        }
        return null;
    }

    public String createRequestJsonAsString(RegisterRequestDto request) {
        String[] realmRoles = { "USER" };
        Map<String, String> clientRoles = new HashMap<>();
        clientRoles.put("auth-service-v1", "READ");

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "firstName", request.getFirstName(),
                    "lastName", request.getLastName(),
                    "email", request.getEmail(),
                    "enabled", true,
                    "realmRoles", Arrays.asList(realmRoles),
                    "clientRoles", clientRoles));
            return json;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
