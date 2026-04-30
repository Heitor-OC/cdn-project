package com.example.cdn.service;

import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.mapper.UploadMapper;
import com.example.cdn.model.Asset;
import com.example.cdn.repository.AssetRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final UploadMapper uploadMapper;

    @Value("${storage.path}")
    private String storagePath;

    public UploadResponseDTO upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        try {

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(storagePath).resolve(filename);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Asset asset = uploadMapper.toEntity(file, filename, path.toString());

            Asset saved = assetRepository.save(asset);


            log.info("O arquivo foi salvo com sucesso: ", filename);

            return uploadMapper.toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("erro ao salvar", e);
        }
    }

    public Resource download(String filename) {
        Asset asset = assetRepository.findByFilename(filename).orElseThrow(() -> new RuntimeException("arquivo nao encontrado"));

        try {
            Path path = Paths.get(asset.getStoragePath());

            Resource resource = new UrlResource(path.toUri());

            log.info("Arquivo carregado: ", filename);

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar o arquivo", e);
        }
    }

}
