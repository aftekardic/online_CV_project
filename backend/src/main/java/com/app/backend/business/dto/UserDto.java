package com.app.backend.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String status;
    private String sub;
    private String email_verified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String family_name;
    private String email;
}
