package com.app.backend.business.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String status;
    private String message;
    private String accessToken;
    private String refreshToken;
}
