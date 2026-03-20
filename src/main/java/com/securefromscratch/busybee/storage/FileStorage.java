package com.securefromscratch.busybee.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

public class FileStorage {
    public enum FileType {
        IMAGE,
        PDF,
        OTHER
    }

    private static final long SAFETY_MARGIN_BYTES = 5L * 1024L * 1024L; // 5MB margin
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L; // 10MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp",
            "image/gif"
    );
    private static final Set<String> ALLOWED_DOC_TYPES = Set.of(
            "application/pdf"
    );

    private final Path m_storagebox;

    public FileStorage(Path storageDirectory) throws IOException {
        m_storagebox = storageDirectory;
        if (!Files.exists(m_storagebox)) {
            Files.createDirectories(m_storagebox);
        }
    }

    public String store(MultipartFile file) throws IOException {
        validateFile(file);

        long fileSize = file.getSize();
        ensureEnoughDiskSpace(fileSize);

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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File is too large");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        contentType = contentType.toLowerCase();

        boolean allowed = ALLOWED_IMAGE_TYPES.contains(contentType) || ALLOWED_DOC_TYPES.contains(contentType);
        if (!allowed) {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private void ensureEnoughDiskSpace(long fileSize) throws IOException {
        FileStore fileStore = Files.getFileStore(m_storagebox);
        long usableSpace = fileStore.getUsableSpace();

        if (usableSpace < fileSize + SAFETY_MARGIN_BYTES) {
            throw new IllegalArgumentException("Not enough disk space");
        }
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

        return filename.substring(lastDot).toLowerCase();
    }
}