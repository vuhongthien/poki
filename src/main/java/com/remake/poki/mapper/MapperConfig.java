package com.remake.poki.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // Bật khớp theo tên trường
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE); // Truy cập trường private
        return modelMapper;
    }
}
