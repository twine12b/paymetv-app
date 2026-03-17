package com.paymetv.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class JsonPayloadCreatorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public File creatJsonFile(Object payload, String fileName) throws IOException {
        File jsonFile = new File("/tmp" + fileName + ".json");
        objectMapper.writeValue(jsonFile, payload);
        return jsonFile;
    }

    public JsonNode createJsonNode(Object payload) {
        return objectMapper.valueToTree(payload);
    }


    // This method produces a lot of ish - using private method as tthis can be fixed later
    public JsonNode prettyPrintJsonString(JsonNode jsonNode) throws JsonProcessingException {
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        return objectMapper.getNodeFactory().textNode(prettyJson);
    }
}
