package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePassword {
    private String email;
    private String password;
}
