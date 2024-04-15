package com.app.gizmophile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product item;

    private String itemColor;
    private String itemVariant;
    private Integer quantity;
    private Double itemValue;
    private Integer returnedQuantity;
    private Integer replacedQuantity;
    private String returnReason;
    private String replaceReason;

    @OneToOne(mappedBy = "orderItem")
    private Review review;

    @ManyToOne
    private Order order;
}
