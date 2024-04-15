package com.app.gizmophile.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    String authToken;
    String username;
}
