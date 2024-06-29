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
public class DBUserDto {
    private String email;
    private String firstName;
    private String lastName;
    private Date birthday;
    private double salary;
}
