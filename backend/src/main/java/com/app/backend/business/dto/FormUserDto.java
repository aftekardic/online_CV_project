package com.app.backend.business.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormUserDto {
    private String sub;
    private String given_name;
    private String family_name;
    private String email;
    private String password;
    private String old_email;
    private Date birthday;
    private double salary;
}
