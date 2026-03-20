package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.storage.FileStorage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class FileViewController {

    @PreAuthorize("@taskAuthorization.canViewImage(#filename, #authentication)")
    @GetMapping("/image")
    public ResponseEntity<ByteArrayResource> getImage(
            @RequestParam("img") String filename,
            Authentication authentication
    ) throws IOException {

        validateFilename(filename);

        MediaType mediaType = detectImageType(filename);

        if (!isAllowedImageType(mediaType)) {
            throw new ResponseStatusException(BAD_REQUEST, "Only image files are allowed");
        }

        FileStorage fileStorage = new FileStorage(Paths.get("uploads"));
        byte[] data;
        try {
            data = fileStorage.getBytes(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(NOT_FOUND, "Image not found");
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(data.length)
                .header("X-Content-Type-Options", "nosniff")
                .body(new ByteArrayResource(data));
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Missing filename");
        }

        Path normalized = Paths.get(filename).normalize();

        if (normalized.isAbsolute()
                || filename.contains("..")
                || filename.contains("/")
                || filename.contains("\\")
                || normalized.getNameCount() != 1) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid filename");
        }
    }

    private boolean isAllowedImageType(MediaType mediaType) {
        return MediaType.IMAGE_PNG.equals(mediaType)
                || MediaType.IMAGE_JPEG.equals(mediaType)
                || MediaType.IMAGE_GIF.equals(mediaType)
                || MediaType.parseMediaType("image/webp").equals(mediaType);
    }

    private MediaType detectImageType(String filename) {
        String lower = filename.toLowerCase();

        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}