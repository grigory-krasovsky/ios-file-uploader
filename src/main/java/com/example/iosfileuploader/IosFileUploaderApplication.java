package com.example.iosfileuploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IosFileUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(IosFileUploaderApplication.class, args);
    }

}
