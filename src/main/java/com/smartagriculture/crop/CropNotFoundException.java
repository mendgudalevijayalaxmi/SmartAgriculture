package com.smartagriculture.crop;

public class CropNotFoundException extends RuntimeException {

    public CropNotFoundException(Long id) {
        super("Crop with id " + id + " was not found");
    }
}
