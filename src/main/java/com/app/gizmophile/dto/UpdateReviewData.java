package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateReviewData {
    private String content;
    private List<String> highlights;
    private List<String> images;
}
