package com.example.fileProcessor.service;

import com.example.fileProcessor.dto.TranslationRequest;
import com.example.fileProcessor.dto.TranslationResponse;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final AiClient ollamaAiClient;

    public TranslationService(AiClient aiClient) {
        this.ollamaAiClient = aiClient;
    }

    public TranslationResponse translate(TranslationRequest request) {

        String prompt = String.format("""
                Translate from %s to %s. Return only translated text. Text: %s""",
                request.getSourceLanguage(), request.getTargetLanguage(), request.getText());

        String resp = ollamaAiClient.generate(prompt);

        TranslationResponse response = new TranslationResponse();
        response.setTranslatedText(resp);
        return response;
    }

}
