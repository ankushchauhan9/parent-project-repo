package com.example.fileProcessor.service;

import com.example.fileProcessor.dto.TranslationRequest;
import com.example.fileProcessor.dto.TranslationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final AiClient aiClient;

    public TranslationService(@Qualifier("OllamaAiClient") AiClient aiClient) {
        this.aiClient = aiClient;
    }

    public TranslationResponse translate(TranslationRequest request) {

        String prompt = String.format("""
                Translate from %s to %s. Return only translated text. Text: %s""",
                request.getSourceLanguage(), request.getTargetLanguage(), request.getText());

        String resp = aiClient.generate(prompt);

        TranslationResponse response = new TranslationResponse();
        response.setTranslatedText(resp);
        return response;
    }

}
