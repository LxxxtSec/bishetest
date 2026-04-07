package com.uav.audit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.uav.audit.mapper")
@EnableScheduling
public class UavAuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(UavAuditApplication.class, args);
    }
}
