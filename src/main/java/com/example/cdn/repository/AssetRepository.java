package com.example.cdn.repository;

import com.example.cdn.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    Asset findByFilename(String filename);
    boolean existsByFilename(String filename);
}
