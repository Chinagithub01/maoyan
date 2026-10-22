package com.maoyan.admin;

import com.maoyan.properties.AliOssProperties;
import com.maoyan.admin.service.AdminAccountService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
        "com.maoyan.admin",
        "com.maoyan.controller.admin",
        "com.maoyan.service",
        "com.maoyan.utils"
})
@EnableConfigurationProperties(AliOssProperties.class)
@MapperScan("com.maoyan.mapper")
public class MaoyanAdmin {
    public static void main(String[] args) {
        SpringApplication.run(MaoyanAdmin.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(AdminAccountService adminAccountService) {
        return args -> adminAccountService.seed();
    }
}
