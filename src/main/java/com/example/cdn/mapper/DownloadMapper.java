package com.example.cdn.mapper;

import com.example.cdn.dto.DownloadResponseDTO;
import com.example.cdn.model.Asset;

public class DownloadMapper {
    public static DownloadResponseDTO toResponse(Asset asset){
        DownloadResponseDTO response = new DownloadResponseDTO();
        response.setId(asset.getId());
        response.setFilename(asset.getFilename());
        response.setStoragePath(asset.getStoragePath());
        response.setContentType(asset.getContentType());
        response.setSize(asset.getSize());
        response.setEtag(asset.getEtag());
        response.setCreatedAt(asset.getCreatedAt());

        return response;
    }
}
