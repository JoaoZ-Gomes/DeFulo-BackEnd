package com.defulo.api.infrastructure.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serve arquivos gravados por {@link FileStorageService} (ex: fotos de evidência de inspeções).
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Arquivos", description = "Acesso a arquivos armazenados no servidor (fotos de evidência, etc.)")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{subpasta}/{nomeArquivo}")
    @Operation(summary = "Baixar um arquivo armazenado", description = "Retorna o conteúdo binário do arquivo (ex: foto de uma inspeção).")
    public ResponseEntity<Resource> servirArquivo(
            @PathVariable String subpasta,
            @PathVariable String nomeArquivo
    ) {
        Resource resource = fileStorageService.carregar(subpasta, nomeArquivo);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
