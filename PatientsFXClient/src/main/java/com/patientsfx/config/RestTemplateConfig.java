package com.patientsfx.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class RestTemplateConfig {

    public static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Настраиваем Jackson конвертер с поддержкой LocalDate
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        
        // Заменяем стандартный конвертер на настроенный
        restTemplate.setMessageConverters(Arrays.asList(converter));
        
        return restTemplate;
    }
}

