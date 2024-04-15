package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUserData {

    private String username;
    private String password;
}
