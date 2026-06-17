package com.tallercopilot.conciliacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring Boot requires public class
public class ConciliacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConciliacionApplication.class, args);
    }
}
