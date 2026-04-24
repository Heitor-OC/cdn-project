package com.example.cdn.service;

import com.example.cdn.model.Asset;
import com.example.cdn.repository.AssetRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;

    @Value("${storage.path}")
    private String storagePath;

    public String upload(MultipartFile file) {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(storagePath).resolve(filename);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        Asset asset = Asset.builder()
                .filename(filename)
                .storagePath(path.toString())
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        assetRepository.save(asset);

        log.info("O arquivo foi salvo com sucesso: ", filename);
        return filename;
    }

    public Resource download(String filename) {
        Asset asset = assetRepository.findByFilename(filename);

        Path path = Paths.get(asset.getStoragePath());

        Resource resource = new UrlResource(path.toUri());

        log.info("Arquivo carregado: ", filename);

        return resource;

    }

}
