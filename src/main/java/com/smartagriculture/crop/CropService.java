package com.smartagriculture.crop;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CropService {

    private final CropRepository cropRepository;

    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @Transactional(readOnly = true)
    public List<CropResponse> findAll() {
        return cropRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(CropResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CropResponse findById(Long id) {
        return CropResponse.from(findCrop(id));
    }

    public CropResponse create(CropRequest request) {
        Crop crop = new Crop(
                request.name().trim(),
                clean(request.variety()),
                request.season(),
                clean(request.soilType()),
                request.expectedYieldKgPerHectare(),
                clean(request.notes()));
        return CropResponse.from(cropRepository.save(crop));
    }

    public CropResponse replace(Long id, CropRequest request) {
        Crop crop = findCrop(id);
        crop.update(
                request.name().trim(),
                clean(request.variety()),
                request.season(),
                clean(request.soilType()),
                request.expectedYieldKgPerHectare(),
                clean(request.notes()));
        return CropResponse.from(cropRepository.save(crop));
    }

    public void delete(Long id) {
        cropRepository.delete(findCrop(id));
    }

    private Crop findCrop(Long id) {
        return cropRepository.findById(id).orElseThrow(() -> new CropNotFoundException(id));
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
