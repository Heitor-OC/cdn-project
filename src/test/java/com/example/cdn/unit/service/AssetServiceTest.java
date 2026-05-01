package com.example.cdn.unit.service;

import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.model.Asset;
import com.example.cdn.mapper.UploadMapper;
import com.example.cdn.repository.AssetRepository;
import com.example.cdn.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UploadMapper uploadMapper;

    @InjectMocks
    private AssetService assetService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(assetService, "storagePath", tempDir.toString());
    }

    @Test
    void shouldUploadFileSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        Asset asset = new Asset();
        asset.setFilename("uuid_test.txt");
        asset.setStoragePath(tempDir.resolve("uuid_test.txt").toString());
        asset.setContentType("text/plain");

        when(uploadMapper.toEntity(eq(file), any(), any())).thenReturn(asset);
        when(assetRepository.save(any())).thenReturn(asset);
        when(uploadMapper.toResponse(any())).thenReturn(
                new UploadResponseDTO(
                        "uuid_test.txt",
                        "text/plain",
                        (long) file.getBytes().length,
                        Instant.now()
                )
        );

        UploadResponseDTO response = assetService.upload(file);

        assertNotNull(response);
        assertEquals("uuid_test.txt", response.getFilename());

        // valida que o arquivo foi realmente salvo no disco
        Path savedPath = tempDir.resolve(asset.getFilename());
        assertTrue(Files.exists(savedPath));

        verify(assetRepository, times(1)).save(any());
        verify(uploadMapper, times(1)).toEntity(eq(file), any(), any());
        verify(uploadMapper, times(1)).toResponse(any());
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.upload(file));

        assertEquals("Arquivo vazio", ex.getMessage());

        verify(assetRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenIOExceptionOccurs() throws Exception {

        MockMultipartFile file = mock(MockMultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.getBytes()).thenThrow(new IOException());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.upload(file));

        assertEquals("erro ao salvar", ex.getMessage());

        verify(assetRepository, never()).save(any());
    }

    @Test
    void shouldDownloadSuccessfully() throws Exception {

        Asset asset = new Asset();
        asset.setFilename("file.txt");
        asset.setStoragePath(tempDir.resolve("file.txt").toString());
        asset.setContentType("text/plain");

        Files.write(Path.of(asset.getStoragePath()), "data".getBytes());

        when(assetRepository.findByFilename("file.txt"))
                .thenReturn(Optional.of(asset));

        Resource resource = assetService.download("file.txt");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void shouldThrowWhenFileNotFoundInDatabase() {

        when(assetRepository.findByFilename("file.txt"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.download("file.txt"));

        assertEquals("Arquivo não encontrado", ex.getMessage());
    }

    @Test
    void shouldThrowWhenFileNotExistsOnDisk() {

        Asset asset = new Asset();
        asset.setFilename("file.txt");
        asset.setStoragePath(tempDir.resolve("file.txt").toString());

        when(assetRepository.findByFilename("file.txt"))
                .thenReturn(Optional.of(asset));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> assetService.download("file.txt"));

        assertEquals("Arquivo nao encontrado ou ilegivel", ex.getMessage());
    }
}