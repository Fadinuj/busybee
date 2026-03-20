package com.securefromscratch.busybee.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileStorage {
    public enum FileType {
        IMAGE,
        PDF,
        OTHER
    }

    private final Path m_storagebox;

    public FileStorage(Path storageDirectory) throws IOException {
        m_storagebox = storageDirectory;
        if (!Files.exists(m_storagebox)) {
            Files.createDirectories(m_storagebox);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        Path target = m_storagebox.resolve(storedFilename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return storedFilename;
    }

    public byte[] getBytes(String filename) throws IOException {
        Path filepath = m_storagebox.resolve(filename);
        return Files.readAllBytes(filepath);
    }

    public static FileType identifyType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return FileType.OTHER;
        }

        contentType = contentType.toLowerCase();

        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        }
        if (contentType.contains("pdf")) {
            return FileType.PDF;
        }
        return FileType.OTHER;
    }

    private static String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDot);
    }
}