package com.app.gizmophile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String email;
    private Long phone;
    private String profile;
    private Long defaultAddress;
    private Integer orders;
    private Integer reviews;
    private Integer addresses;
    private List<String> wishlistItems;
    private List<String> cartItems;
    private List<String> browsedItems;

}
