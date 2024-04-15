package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feedback {
    private String feedback;
    private String feedbackType;
}
