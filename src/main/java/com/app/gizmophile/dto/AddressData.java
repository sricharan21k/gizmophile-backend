package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressData {
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
    private Boolean isDefault;
}