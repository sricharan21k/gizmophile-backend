package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.DoubleBuffer;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderData {
    private Long id;
    private String orderId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private Double deliveryCharge;
    private Double orderAmount;
    private String paymentMode;
    private String status;
    private Long shippingAddress;
    private String feedback;
    private String feedbackType;
    private List<OrderItemData> items;
}
