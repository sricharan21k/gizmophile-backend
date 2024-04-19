package com.app.gizmophile.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemData {
    private Long id;
    private Long item;
    private String type;
    private String itemName;
    private String color;
    private String variant;
    private Integer quantity;
    private Integer rating;
    private Integer returnedQuantity;
    private Integer replacedQuantity;
    private String returnReason;
    private String replaceReason;
    private Double price;
    private Double itemValue;
    private String imageUrl;
    private Boolean isReviewed;
    private Long reviewId;


}
