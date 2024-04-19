package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchProduct {
    private Long productId;
    private String productName;
    private Long colorId;
    private Long variantId;
    private String imageUrl;
}