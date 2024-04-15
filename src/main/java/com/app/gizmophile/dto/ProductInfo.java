package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    private Long productId;
    private String productType;
    private String productName;
    private String productColor;
    private String productVariant;
    private Long productImage;
    private String description;
    private Integer stock;
    private Integer orderCount;
}
