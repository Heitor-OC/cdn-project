package com.example.cdn.unit.service;

import com.example.cdn.dto.UploadResponseDTO;
import com.example.cdn.model.Asset;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {

        ReflectionTestUtils.setField(
                assetService,
                "storagePath",
                tempDir.toString()
        );
    }

    @Test
    void shouldUploadFileSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        when(assetRepository.save(any(Asset.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UploadResponseDTO response =
                assetService.upload(file);

        assertNotNull(response);

        assertEquals("text/plain", response.getContentType());

        assertEquals(file.getBytes().length, response.getSize());

        Path savedPath =
                tempDir.resolve(response.getFilename());

        assertTrue(Files.exists(savedPath));

        verify(assetRepository, times(1))
                .save(any(Asset.class));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> assetService.upload(file)
        );

        assertEquals(
                "Arquivo vazio",
                ex.getMessage()
        );

        verify(assetRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenIOExceptionOccurs() throws Exception {

        MockMultipartFile file =
                mock(MockMultipartFile.class);

        when(file.isEmpty()).thenReturn(false);

        when(file.getOriginalFilename())
                .thenReturn("file.txt");

        when(file.getBytes())
                .thenThrow(new IOException());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> assetService.upload(file)
        );

        assertEquals(
                "erro ao salvar",
                ex.getMessage()
        );

        verify(assetRepository, never())
                .save(any());
    }

    @Test
    void shouldDownloadSuccessfully() throws Exception {

        Asset asset = new Asset();
        asset.setFilename("file.txt");
        asset.setStoragePath(
                tempDir.resolve("file.txt").toString()
        );
        asset.setContentType("text/plain");

        Files.write(
                Path.of(asset.getStoragePath()),
                "data".getBytes()
        );

        when(assetRepository.existsByFilename("file.txt"))
                .thenReturn(true);

        when(assetRepository.findByFilename("file.txt"))
                .thenReturn(asset);

        Resource resource =
                assetService.download("file.txt");

        assertNotNull(resource);

        assertTrue(resource.exists());

        assertTrue(resource.isReadable());

        verify(assetRepository, times(1))
                .existsByFilename("file.txt");

        verify(assetRepository, times(1))
                .findByFilename("file.txt");
    }

    @Test
    void shouldThrowWhenFileNotFoundInDatabase() {

        when(assetRepository.existsByFilename("file.txt"))
                .thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> assetService.download("file.txt")
        );

        assertEquals(
                "Arquivo nao encontrado: file.txt",
                ex.getMessage()
        );
    }

    @Test
    void shouldThrowWhenFileNotExistsOnDisk() {

        Asset asset = new Asset();

        asset.setFilename("file.txt");

        asset.setStoragePath(
                tempDir.resolve("file.txt").toString()
        );

        when(assetRepository.existsByFilename("file.txt"))
                .thenReturn(true);

        when(assetRepository.findByFilename("file.txt"))
                .thenReturn(asset);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> assetService.download("file.txt")
        );

        assertEquals(
                "Arquivo nao encontrado ou ilegivel",
                ex.getMessage()
        );
    }
}