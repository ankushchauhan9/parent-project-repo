package com.example.fileProcessor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("OllamaAiClient")
public class OllamaAiClient implements AiClient {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${ollama.url}")
    private String aiClientURl;

    @Override
    public String generate(String prompt) {
        // TODO Auto-generated method stub

        Map<String, Object> request = new HashMap<>();
        request.put("model", "mistral");
        request.put("prompt", prompt);
        request.put("stream", false);

        Map response = restTemplate.postForObject(aiClientURl, request, Map.class);
        return (String) response.get("response");
    }
}
