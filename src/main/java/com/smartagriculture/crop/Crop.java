package com.smartagriculture.crop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String variety;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CropSeason season;

    @Column(name = "soil_type", length = 100)
    private String soilType;

    @Column(name = "expected_yield_kg_per_hectare", precision = 10, scale = 2)
    private BigDecimal expectedYieldKgPerHectare;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Crop() {
    }

    public Crop(
            String name,
            String variety,
            CropSeason season,
            String soilType,
            BigDecimal expectedYieldKgPerHectare,
            String notes) {
        this.name = name;
        this.variety = variety;
        this.season = season;
        this.soilType = soilType;
        this.expectedYieldKgPerHectare = expectedYieldKgPerHectare;
        this.notes = notes;
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVariety() {
        return variety;
    }

    public CropSeason getSeason() {
        return season;
    }

    public String getSoilType() {
        return soilType;
    }

    public BigDecimal getExpectedYieldKgPerHectare() {
        return expectedYieldKgPerHectare;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(
            String name,
            String variety,
            CropSeason season,
            String soilType,
            BigDecimal expectedYieldKgPerHectare,
            String notes) {
        this.name = name;
        this.variety = variety;
        this.season = season;
        this.soilType = soilType;
        this.expectedYieldKgPerHectare = expectedYieldKgPerHectare;
        this.notes = notes;
    }
}
