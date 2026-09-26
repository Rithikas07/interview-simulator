package com.interviewsim.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * {@link LlmClient} implementation for the Google Gemini API (free tier
 * available via Google AI Studio).
 * <p>
 * Configured via environment variables:
 * {@code GEMINI_API_KEY} (required) and {@code GEMINI_MODEL}
 * (optional, default {@code gemini-2.5-flash}).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class GeminiLlmClient implements LlmClient {

    /** Native Gemini generate-content endpoint template. */
    private static final String API_URL_TEMPLATE =
            "https://gemini.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    /** Default model on the free tier. */
    private static final String DEFAULT_MODEL = "gemini-2.5-flash";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();
    private final String apiKey;
    private final String model;

    /**
     * Creates the client using environment configuration.
     */
    public GeminiLlmClient() {
        this(System.getenv("GEMINI_API_KEY"),
                System.getenv().getOrDefault("GEMINI_MODEL", DEFAULT_MODEL));
    }

    /**
     * Creates the client with explicit credentials (useful for tests).
     *
     * @param apiKey the Gemini API key (may be null)
     * @param model  the model identifier
     */
    public GeminiLlmClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** {@inheritDoc} */
    @Override
    public String getProviderName() {
        return "Gemini";
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: POSTs to {@code generateContent} with a system
     * instruction and extracts the first candidate's text.</p>
     */
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        JsonObject body = new JsonObject();
        JsonObject userContent = new JsonObject();
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", userPrompt);
        userContent.add("parts", gson.toJsonTree(new JsonObject[]{userPart}));
        userContent.addProperty("role", "user");
        body.add("contents", gson.toJsonTree(new JsonObject[]{userContent}));

        JsonObject sysPart = new JsonObject();
        sysPart.addProperty("text", systemPrompt);
        JsonObject sysInstruction = new JsonObject();
        sysInstruction.add("parts", gson.toJsonTree(new JsonObject[]{sysPart}));
        body.add("systemInstruction", sysInstruction);

        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("maxOutputTokens", 1000);
        genConfig.addProperty("temperature", 0.4);
        body.add("generationConfig", genConfig);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL_TEMPLATE, model, apiKey)))
                .timeout(Duration.ofSeconds(60))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try {
            HttpResponse<String> resp =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new AiEvaluationException(
                        "Gemini API returned HTTP " + resp.statusCode());
            }
            return gson.fromJson(resp.body(), JsonObject.class)
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (IOException e) {
            throw new AiEvaluationException(
                    "Network error calling Gemini API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiEvaluationException(
                    "Interrupted while calling Gemini API", e);
        } catch (AiEvaluationException e) {
            throw e;
        } catch (Exception e) {
            throw new AiEvaluationException(
                    "Unexpected Gemini response shape", e);
        }
    }
}
