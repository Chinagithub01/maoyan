package com.maoyan.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.maoyan.constant.AdminConstant;
import com.maoyan.properties.AliOssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class AliOssService {
    private static final Logger log = LoggerFactory.getLogger(AliOssService.class);
    private final AliOssProperties properties;

    public AliOssService(AliOssProperties properties) {
        this.properties = properties;
    }

    public String upload(InputStream inputStream, String extension) {
        String objectName = "show/poster/" + UUID.randomUUID() + extension;
        OSS client = new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret());
        try {
            client.putObject(properties.getBucketName(), objectName, inputStream);
        } catch (RuntimeException exception) {
            log.warn("海报上传到阿里云失败: {}", exception.getMessage());
            throw new IllegalArgumentException(AdminConstant.POSTER_OSS_FAILED);
        } finally {
            client.shutdown();
        }
        return "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/" + objectName;
    }
}
