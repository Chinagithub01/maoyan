package com.maoyan.admin.oss;

import com.maoyan.constant.AdminConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PosterTempStore {
    private final Path directory = Path.of(System.getProperty("java.io.tmpdir"), "maoyan-poster");
    private final Map<String, StoredPoster> files = new ConcurrentHashMap<>();

    public StoredPoster save(MultipartFile file) throws IOException {
        String extension = extensionOf(file);
        Files.createDirectories(directory);
        String token = UUID.randomUUID().toString().replace("-", "");
        Path path = directory.resolve(token + extension);
        file.transferTo(path);
        StoredPoster stored = new StoredPoster(token, path, file.getOriginalFilename(), contentType(extension));
        files.put(token, stored);
        return stored;
    }

    public StoredPoster get(String token) {
        StoredPoster stored = files.get(token);
        if (stored == null || !Files.exists(stored.path())) {
            throw new IllegalArgumentException(AdminConstant.POSTER_EXPIRED);
        }
        return stored;
    }

    public void delete(String token) {
        StoredPoster stored = files.remove(token);
        if (stored != null) {
            try {
                Files.deleteIfExists(stored.path());
            } catch (IOException ignored) {
                // 临时文件留在系统临时目录，不影响已上传到阿里云的海报。
            }
        }
    }

    public static String extensionOf(MultipartFile file) {
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return ".jpg";
        }
        if (name.endsWith(".png")) {
            return ".png";
        }
        if (name.endsWith(".webp")) {
            return ".webp";
        }
        if (name.endsWith(".gif")) {
            return ".gif";
        }
        throw new IllegalArgumentException(AdminConstant.POSTER_TYPE);
    }

    private String contentType(String extension) {
        return switch (extension) {
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            case ".gif" -> "image/gif";
            default -> "image/jpeg";
        };
    }

    public record StoredPoster(String token, Path path, String fileName, String contentType) {
        public InputStream open() throws IOException {
            return Files.newInputStream(path);
        }
    }
}
