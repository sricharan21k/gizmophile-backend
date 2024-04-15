package com.app.gizmophile.dto;

import lombok.Data;

@Data
public class ValidateOTP {
    private String email;
    private String code;
}