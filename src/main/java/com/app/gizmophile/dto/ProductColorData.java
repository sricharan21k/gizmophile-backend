package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductColorData {
    private Long id;
    private Long productId;
    private String color;
    private String image;
}
