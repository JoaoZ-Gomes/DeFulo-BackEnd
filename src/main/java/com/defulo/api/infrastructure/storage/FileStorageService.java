package com.defulo.api.infrastructure.storage;

import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Armazenamento de arquivos em disco local (fotos de evidência de inspeções, etc.).
 * Substitui o antigo stub que descartava os bytes recebidos e devolvia uma URL fictícia.
 */
@Slf4j
@Service
public class FileStorageService {

    @Value("${app.storage.base-path:./storage}")
    private String basePath;

    /**
     * Decodifica uma imagem Base64 (com ou sem prefixo "data:image/...;base64,") e
     * grava em disco sob {@code basePath/subpasta/nomeArquivo.jpg}.
     *
     * @return caminho relativo para acesso via {@code GET /api/files/{subpasta}/{arquivo}}
     */
    public String salvarImagemBase64(String base64, String subpasta, String nomeArquivo) {
        try {
            String base64Puro = base64.contains(",") ? base64.substring(base64.indexOf(',') + 1) : base64;
            byte[] bytes = Base64.getDecoder().decode(base64Puro);

            Path diretorio = Paths.get(basePath, subpasta).normalize();
            Files.createDirectories(diretorio);

            String arquivo = nomeArquivo + ".jpg";
            Path destino = diretorio.resolve(arquivo).normalize();
            Files.write(destino, bytes);

            log.info("Arquivo salvo em {}", destino);
            return "/api/files/" + subpasta + "/" + arquivo;
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Falha ao salvar arquivo '" + nomeArquivo + "': " + e.getMessage(), e);
        }
    }

    /** Carrega um arquivo previamente salvo, validando que o caminho resolvido não escapa da subpasta. */
    public Resource carregar(String subpasta, String nomeArquivo) {
        try {
            Path diretorio = Paths.get(basePath, subpasta).normalize();
            Path arquivo = diretorio.resolve(nomeArquivo).normalize();

            if (!arquivo.startsWith(diretorio)) {
                throw new RecursoNaoEncontradoException("Arquivo não encontrado: " + nomeArquivo);
            }

            Resource resource = new UrlResource(arquivo.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RecursoNaoEncontradoException("Arquivo não encontrado: " + nomeArquivo);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RecursoNaoEncontradoException("Arquivo não encontrado: " + nomeArquivo);
        }
    }
}
