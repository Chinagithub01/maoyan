package com.maoyan;

import com.maoyan.constant.AppConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(
        basePackages = "com.maoyan",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.maoyan\\.controller\\.admin\\..*"
                ),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.maoyan\\.utils\\..*"
                )
        }
)
@MapperScan(AppConstant.MAPPER_PACKAGE)
@Slf4j
public class Maoyan {
    public static void main(String[] args) {
        SpringApplication.run(Maoyan.class, args);
        log.info(AppConstant.STARTED);
    }
}
