package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserInfo {
    private String username;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String email;
    private Long phone;
    private String profile;
}
