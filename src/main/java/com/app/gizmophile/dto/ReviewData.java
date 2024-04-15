package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewData {
    private Long id;
    private Integer rating;
    private String content;
    private LocalDateTime posted;
    private LocalDateTime lastUpdated;
    private Integer likes;
    private Integer replies;
    private UserProfile profile;
    private ProductInfo product;
    private List<String> images;
    private List<String> highlights;
}
