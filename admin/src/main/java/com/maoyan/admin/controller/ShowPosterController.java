package com.maoyan.admin.controller;

import com.maoyan.utils.AliOssService;
import com.maoyan.admin.oss.PosterTempStore;
import com.maoyan.constant.AdminConstant;
import com.maoyan.result.Result;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/admin/show/poster")
public class ShowPosterController {
    private final PosterTempStore posterTempStore;
    private final AliOssService aliOssService;

    public ShowPosterController(PosterTempStore posterTempStore, AliOssService aliOssService) {
        this.posterTempStore = posterTempStore;
        this.aliOssService = aliOssService;
    }

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String message = invalid(file);
        if (message != null) {
            return Result.error(message);
        }
        try {
            PosterTempStore.StoredPoster stored = posterTempStore.save(file);
            return Result.success(Map.of(
                    "token", stored.token(),
                    "fileName", stored.fileName() == null ? "poster" + PosterTempStore.extensionOf(file) : stored.fileName()));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        } catch (IOException exception) {
            return Result.error(AdminConstant.POSTER_OSS_FAILED);
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> download(@PathVariable String token) throws IOException {
        try {
            PosterTempStore.StoredPoster stored = posterTempStore.get(token);
            String fileName = URLEncoder.encode(
                    stored.fileName() == null ? "poster" : stored.fileName(),
                    StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(stored.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                    .body(new InputStreamResource(stored.open()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Result.error(exception.getMessage()));
        }
    }

    @PostMapping("/oss")
    public Result<Map<String, String>> uploadToAliyun(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "token", required = false) String token) {
        String message = invalid(file);
        if (message != null) {
            return Result.error(message);
        }
        try {
            String url = aliOssService.upload(file.getInputStream(), PosterTempStore.extensionOf(file));
            if (token != null && !token.isBlank()) {
                posterTempStore.delete(token);
            }
            return Result.success(Map.of("url", url));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        } catch (IOException exception) {
            return Result.error(AdminConstant.POSTER_OSS_FAILED);
        }
    }

    private String invalid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return AdminConstant.POSTER_REQUIRED;
        }
        if (file.getSize() > AdminConstant.POSTER_MAX_BYTES) {
            return AdminConstant.POSTER_TOO_LARGE;
        }
        return null;
    }
}
