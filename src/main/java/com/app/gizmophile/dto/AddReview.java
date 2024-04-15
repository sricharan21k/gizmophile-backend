package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddReview {
    private String username;
    private Long productId;
    private Long orderId;
    private Long orderItemId;
    private String content;
    private List<String> highlights;
    private List<String> images;
}
