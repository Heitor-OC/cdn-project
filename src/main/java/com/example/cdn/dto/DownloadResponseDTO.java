package com.example.cdn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponseDTO {

    private String filename;
    private String contentType;
    private Long size;
    private Instant createdAt;
    private String url;
}
