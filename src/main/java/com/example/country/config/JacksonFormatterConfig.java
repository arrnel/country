package com.example.country.config;

import com.example.country.jackson.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class JacksonFormatterConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new SimpleModule("CustomLocalDateTimeSerializer")
                        .addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer()));
    }

}
