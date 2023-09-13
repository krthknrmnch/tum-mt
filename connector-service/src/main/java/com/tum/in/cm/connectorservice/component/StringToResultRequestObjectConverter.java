package com.tum.in.cm.connectorservice.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ResultRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToResultRequestObjectConverter implements Converter<String, ResultRequestObject> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResultRequestObject convert(String source) {
        try {
            return objectMapper.readValue(source, ResultRequestObject.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
