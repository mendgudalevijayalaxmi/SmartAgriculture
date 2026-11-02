package com.smartagriculture.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CropRepository extends JpaRepository<Crop, Long>, JpaSpecificationExecutor<Crop> {
}
