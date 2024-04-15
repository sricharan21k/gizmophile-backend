package com.app.gizmophile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String state;
    private String city;
    private String street;
    private Long zipcode;
    private String addressType;
    private String house;
    private String landmark;
    private String directions;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "shippingAddress")
    private List<Order> orders = new ArrayList<>();
}
