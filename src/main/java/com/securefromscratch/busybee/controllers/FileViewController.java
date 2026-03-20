package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.storage.FileStorage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;

@RestController
public class FileViewController {

    @GetMapping("/image")
    public ResponseEntity<ByteArrayResource> getImage(@RequestParam("img") String filename) throws IOException {
        FileStorage fileStorage = new FileStorage(Paths.get("uploads"));
        byte[] data = fileStorage.getBytes(filename);

        MediaType mediaType = detectImageType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/attachment")
    public ResponseEntity<ByteArrayResource> getAttachment(@RequestParam("file") String filename) throws IOException {
        FileStorage fileStorage = new FileStorage(Paths.get("uploads"));
        byte[] data = fileStorage.getBytes(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
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