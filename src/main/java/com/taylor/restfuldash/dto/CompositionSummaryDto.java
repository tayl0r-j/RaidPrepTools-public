package com.taylor.restfuldash.dto;

import com.taylor.restfuldash.model.PlayerClasses;
import com.taylor.restfuldash.model.PlayerSpec;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompositionSummaryDto {
    private PlayerClasses playerClass;
    private PlayerSpec playerSpec;
    private Long count;
}
