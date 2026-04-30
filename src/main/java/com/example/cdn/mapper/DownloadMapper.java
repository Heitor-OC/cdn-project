package com.example.cdn.mapper;

import com.example.cdn.dto.DownloadResponseDTO;
import com.example.cdn.model.Asset;

public class DownloadMapper {
    public static DownloadResponseDTO toResponse(Asset asset){
        DownloadResponseDTO response = new DownloadResponseDTO();

        response.setFilename(asset.getFilename());
        response.setContentType(asset.getContentType());
        response.setSize(asset.getSize());
        response.setCreatedAt(asset.getCreatedAt());
        response.setUrl("/cdn/" + asset.getFilename());

        return response;
    }
}
