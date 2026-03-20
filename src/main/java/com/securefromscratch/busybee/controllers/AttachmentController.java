package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.storage.FileStorage;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.HttpStatus.*;

@RestController
public class AttachmentController {

    @GetMapping("/attachment")
    public ResponseEntity<byte[]> getAttachment(@RequestParam("file") String filename) throws IOException {
        validateFilename(filename);

        FileStorage storage = new FileStorage(Paths.get("uploads"));
        byte[] data;
        try {
            data = storage.getBytes(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(NOT_FOUND, "Attachment not found");
        }

        MediaType mediaType = resolveAttachmentMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(data);
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

    private MediaType resolveAttachmentMediaType(String filename) {
        String lower = filename.toLowerCase();

        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}