package com.app.gizmophile.dto;

import lombok.Data;

@Data
public class NewUser {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
}
