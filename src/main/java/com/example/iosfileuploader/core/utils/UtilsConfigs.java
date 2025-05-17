package com.example.iosfileuploader.core.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class UtilsConfigs {
    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }
}