package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.infraestrutura.ArmazenamentoArquivoException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads/perfil").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new ArmazenamentoArquivoException(
                    "Não foi possível criar o diretório onde os arquivos serão armazenados.", ex);
        }
    }

    public String armazenarArquivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Não é possível enviar um arquivo vazio.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("A imagem não pode ultrapassar 5 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Apenas arquivos de imagem são permitidos.");
        }

        try {
            String originalFileName = org.springframework.util.StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "" : file.getOriginalFilename()
            );
            String extension = extensionOf(originalFileName);
            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
                throw new IllegalArgumentException("Formato de imagem não permitido.");
            }

            String fileName = UUID.randomUUID() + extension;

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "perfil/" + fileName;

        } catch (IOException ex) {
            throw new ArmazenamentoArquivoException("Não foi possível armazenar o arquivo.", ex);
        }
    }

    private String extensionOf(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot >= 0
                ? fileName.substring(lastDot).toLowerCase(Locale.ROOT)
                : "";
    }
}