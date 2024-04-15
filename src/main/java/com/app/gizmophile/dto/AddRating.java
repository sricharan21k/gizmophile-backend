package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddRating {
    private Long orderId;
    private Long orderItemId;
    private Integer rating;
    private String username;
}
