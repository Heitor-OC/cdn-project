package com.example.cdn.unit.controller;

import com.example.cdn.controller.AssetController;
import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.model.Asset;
import com.example.cdn.service.AssetService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @Test
    void shouldUploadFileSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        UploadResponseDTO responseDTO = new UploadResponseDTO(
                "file.txt",
                "text/plain",
                100L,
                Instant.now()
        );

        when(assetService.upload(any())).thenReturn(responseDTO);

        mockMvc.perform(multipart("/cdn/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("file.txt"))
                .andExpect(jsonPath("$.contentType").value("text/plain"))
                .andExpect(jsonPath("$.size").value(100L));
    }

    @Test
    void shouldReturnErrorWhenUploadFails() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        when(assetService.upload(any()))
                .thenThrow(new RuntimeException("Arquivo vazio"));

        mockMvc.perform(multipart("/cdn/upload").file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldDownloadFileSuccessfully() throws Exception {

        Asset asset = new Asset();
        asset.setFilename("file.txt");
        asset.setContentType("text/plain");

        Resource resource = new ByteArrayResource("conteudo".getBytes());

        when(assetService.getAsset("file.txt")).thenReturn(asset);
        when(assetService.loadResource(asset)).thenReturn(resource);

        mockMvc.perform(get("/cdn/file.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"file.txt\""
                ))
                .andExpect(content().contentType("text/plain"));
    }

    @Test
    void shouldReturnErrorWhenFileNotFound() throws Exception {

        when(assetService.getAsset("file.txt"))
                .thenThrow(new RuntimeException("Arquivo não encontrado"));

        mockMvc.perform(get("/cdn/file.txt"))
                .andExpect(status().isInternalServerError());
    }
}