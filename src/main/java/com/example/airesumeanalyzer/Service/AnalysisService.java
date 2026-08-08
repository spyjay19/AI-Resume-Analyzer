package com.example.airesumeanalyzer.Service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {
    @Value("${groq.api.key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeResume(String resumeText) throws IOException{
        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of("role", "user", "content", buildPrompt(resumeText))
                )
        );

        String json = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("content-type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println("API Response: " + responseBody);
            Map<String, Object> parsed = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) parsed.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
    }

    private String buildPrompt(String resumeText){
        return """
                You are an expect resume reviewer. Analyze the following resume and provide:
                
                1. STRENGTHS: List 3-5 Key strengths
                2. WEAKNESSES: List 3-5 areas for improvement
                3. SUGGESTIONS: Give 3-5 specific actionable suggestions
                4. SCORE: Give an overall score out of 100 with a brief explanation
                
                Resume:
                """ + resumeText;
    }
}
