package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AddReply {
    private Long userId;
    private Long reviewId;
    private String content;
}
