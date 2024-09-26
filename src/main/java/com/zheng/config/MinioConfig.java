package com.zheng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio配置
 */
@Configuration
public class MinioConfig {
    @Bean
    public io.minio.MinioClient minioClient() {
        return io.minio.MinioClient.builder().
                endpoint("http://localhost:9000").
                credentials("minioadmin", "minioadmin").
                build();
    }
}
