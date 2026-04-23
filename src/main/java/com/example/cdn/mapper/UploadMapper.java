package com.example.cdn.mapper;

import com.example.cdn.dto.UploadRequestDTO;
import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.model.Asset;

public class UploadMapper {
    public static Asset toEntity(UploadRequestDTO dto){
        Asset asset = new Asset();
        asset.setFilename(dto.getFilename());
        asset.setContentType(dto.getContentType());
        asset.setSize(dto.getSize());

        return asset;
    }

    public static UploadResponseDTO toResponse(Asset asset){
        UploadResponseDTO response = new UploadResponseDTO();
        response.setId(asset.getId());
        response.setFilename(asset.getFilename());
        response.setContentType(asset.getContentType());
        response.setSize(asset.getSize());
        response.setStoragePath(asset.getStoragePath());
        response.setEtag(asset.getEtag());
        response.setCreatedAt(asset.getCreatedAt());

        return response;
    }
}
