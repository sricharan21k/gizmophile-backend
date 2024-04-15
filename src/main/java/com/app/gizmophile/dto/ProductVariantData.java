package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantData {
    private Long id;
    private Long productId;
    private String variant;
    private Double price;
}
