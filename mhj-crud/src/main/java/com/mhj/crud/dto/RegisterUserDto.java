package com.mhj.crud.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String email;
    private String password;
    private String fullName;
}
