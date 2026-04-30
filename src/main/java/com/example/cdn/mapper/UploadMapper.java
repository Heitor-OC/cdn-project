package com.example.cdn.mapper;

import com.example.cdn.dto.UploadRequestDTO;
import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.model.Asset;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Component
public class UploadMapper {

    public static Asset toEntity(MultipartFile file, String filename, String storagePath){

        return Asset.builder()
                .filename(filename)
                .storagePath(storagePath)
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdAt(Instant.now())
                .build();
    }

    public static UploadResponseDTO toResponse(Asset asset){
        UploadResponseDTO response = new UploadResponseDTO();
        response.setFilename(asset.getFilename());
        response.setContentType(asset.getContentType());
        response.setSize(asset.getSize());
        response.setCreatedAt(asset.getCreatedAt());

        return response;
    }
}
