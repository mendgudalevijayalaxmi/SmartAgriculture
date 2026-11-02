package com.smartagriculture.crop;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CropRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 100) String variety,
        @NotNull CropSeason season,
        @Size(max = 100) String soilType,
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 8, fraction = 2)
        BigDecimal expectedYieldKgPerHectare,
        @Size(max = 1000) String notes) {
}
