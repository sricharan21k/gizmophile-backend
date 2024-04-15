package com.app.gizmophile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnOrReplaceData {
    private Long itemId;
    private Integer quantity;
    private String reason;
}
