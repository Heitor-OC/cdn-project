package com.example.cdn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDTO {

    private UUID id;
    private String filename;
    private String storagePath;
    private String contentType;
    private Long size;
    private String etag;
    private Instant createdAt;
}
