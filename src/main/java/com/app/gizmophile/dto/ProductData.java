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
public class ProductData {
    private Long id;
    private String type;
    private String brand;
    private String model;
    private String description;
    private Integer stock;
    private Integer orderCount;
    private Double baseVariant;
    private Double rating;
    private List<String> specs;
    private List<Long> variants;
    private List<Long> colors;
}
