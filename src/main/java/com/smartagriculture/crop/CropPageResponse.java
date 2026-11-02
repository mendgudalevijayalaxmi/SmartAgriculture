package com.smartagriculture.crop;

import java.util.List;
import org.springframework.data.domain.Page;

public record CropPageResponse(
        List<CropResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext) {

    static CropPageResponse from(Page<CropResponse> result) {
        return new CropPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext());
    }
}
