package com.project.payments.util;

import java.util.LinkedHashMap;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public <T> T convertJsonToObject(String json, Class<T> clazz) {
        if (json == null || clazz == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to convert JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    public String convertObjectToJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON: {}", e.getMessage());
            return null;
        }
    }
    
    public String prepareFormattedJson(String body) {
        if (body == null || body.isBlank()) return "";
        try {
            LinkedHashMap<String, Object> map = objectMapper.readValue(body, LinkedHashMap.class);
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Error while formatting JSON body: {}", e.getMessage());
            return null;
        }
    }
}