package com.app.backend.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String access_token;
    private String refresh_token;
    private int expires_in;
    private int refresh_expires_in;
}
