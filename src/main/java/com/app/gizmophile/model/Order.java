package com.app.gizmophile.model;

import com.app.gizmophile.enumtype.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private Double deliveryCharge;
    private Double orderAmount;
    private String paymentMode;
    private String feedbackType;
    private String feedback;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne
    private Address shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    private User user;
}
