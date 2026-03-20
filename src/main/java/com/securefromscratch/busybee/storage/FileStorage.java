package com.securefromscratch.busybee.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class FileStorage {
    public enum FileType {
        IMAGE,
        PDF,
        OTHER
    }

    private static final long MAX_REMOTE_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

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

    public String storeRemoteImage(String imageUrl) throws IOException, InterruptedException {
        URI uri;
        try {
            uri = URI.create(imageUrl);
        } catch (Exception e) {
            throw new IOException("Invalid image URL");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new IOException("Only http/https image links are allowed");
        }

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", "BusyBee/1.0")
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Failed to download image, status: " + status);
        }

        String contentType = firstHeader(response, "Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IOException("Remote resource is not an image");
        }

        String contentLengthHeader = firstHeader(response, "Content-Length");
        if (contentLengthHeader != null) {
            try {
                long len = Long.parseLong(contentLengthHeader);
                if (len > MAX_REMOTE_IMAGE_SIZE) {
                    throw new IOException("Remote image too large");
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String extension = extensionFromContentType(contentType);
        if (extension.isBlank()) {
            extension = extractExtension(uri.getPath());
        }
        if (extension.isBlank()) {
            extension = ".img";
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path target = m_storagebox.resolve(storedFilename);

        try (InputStream in = response.body()) {
            copyWithLimit(in, target, MAX_REMOTE_IMAGE_SIZE);
        }

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

    private static String firstHeader(HttpResponse<?> response, String name) {
        List<String> values = response.headers().allValues(name);
        return values.isEmpty() ? null : values.get(0);
    }

    private static void copyWithLimit(InputStream in, Path target, long maxBytes) throws IOException {
        long total = 0;
        byte[] buffer = new byte[8192];

        try (var out = Files.newOutputStream(target)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    out.close();
                    Files.deleteIfExists(target);
                    throw new IOException("Remote image too large");
                }
                out.write(buffer, 0, read);
            }
        }
    }

    private static String extensionFromContentType(String contentType) {
        String lower = contentType.toLowerCase();
        if (lower.startsWith("image/png")) return ".png";
        if (lower.startsWith("image/jpeg")) return ".jpg";
        if (lower.startsWith("image/gif")) return ".gif";
        if (lower.startsWith("image/webp")) return ".webp";
        if (lower.startsWith("image/svg+xml")) return ".svg";
        return "";
    }

    private static String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int lastSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        String cleanName = (lastSlash >= 0) ? filename.substring(lastSlash + 1) : filename;

        int lastDot = cleanName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == cleanName.length() - 1) {
            return "";
        }

        return cleanName.substring(lastDot);
    }
}