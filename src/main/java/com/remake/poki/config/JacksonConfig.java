package com.remake.poki.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // ✅ ĐĂNG KÝ JSR310 MODULE
        mapper.registerModule(new JavaTimeModule());

        // ✅ DISABLE WRITE_DATES_AS_TIMESTAMPS (viết dạng ISO-8601)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}