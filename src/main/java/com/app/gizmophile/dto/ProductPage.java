package com.app.gizmophile.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductPage {
    private List<ProductData> products;
    private Long totalProducts;
    private Integer totalPages;
}