package com.fiap.contratAI_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.model:gemini-2.0-flash-exp}")
    private String modelName;

    @Bean
    public String geminiModelName() {
        return modelName;
    }
}
