package com.taylor.restfuldash.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotUpdateRequest {
    private Integer groupIndex;
    private Integer slotIndex;
}
