package com.smartagriculture.crop;

import java.math.BigDecimal;
import java.time.Instant;

public record CropResponse(
        Long id,
        String name,
        String variety,
        CropSeason season,
        String soilType,
        BigDecimal expectedYieldKgPerHectare,
        String notes,
        Instant createdAt,
        Instant updatedAt) {

    static CropResponse from(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getName(),
                crop.getVariety(),
                crop.getSeason(),
                crop.getSoilType(),
                crop.getExpectedYieldKgPerHectare(),
                crop.getNotes(),
                crop.getCreatedAt(),
                crop.getUpdatedAt());
    }
}
