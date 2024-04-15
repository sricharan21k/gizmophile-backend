package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReplyData {
    private String content;
    private LocalDateTime lastUpdated;
    private UserProfile profile;

}
