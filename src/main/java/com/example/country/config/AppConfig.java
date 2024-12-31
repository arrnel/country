package com.example.country.config;

import com.example.country.service.AppErrorAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.api.version}")
    private String apiVersion;

    @Bean
    public ErrorAttributes errorAttributes() {
        return new AppErrorAttributes(apiVersion);
    }

}
