package com.miapp.usersmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EntityScan(basePackages = "com.miapp.sistemasdistribuidos.entity")
@SpringBootApplication
public class UsersMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersMicroserviceApplication.class, args);
    }

}
