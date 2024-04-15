package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfile {
    private String username;
    private String profilePicture;
}
