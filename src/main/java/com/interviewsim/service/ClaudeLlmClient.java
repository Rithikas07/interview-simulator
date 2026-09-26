package com.interviewsim.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link LlmClient} implementation for the Anthropic Claude Messages API.
 * <p>
 * Configured via environment variables:
 * {@code ANTHROPIC_API_KEY} (required) and {@code ANTHROPIC_MODEL}
 * (optional, default {@code claude-sonnet-4-5}).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class ClaudeLlmClient implements LlmClient {

    /** Anthropic Messages API endpoint. */
    private static final String API_URL = "https://api.anthropic.com/v1/messages";

    /** API version header required by Anthropic. */
    private static final String API_VERSION = "2023-06-01";

    /** Default model used when {@code ANTHROPIC_MODEL} is not set. */
    private static final String DEFAULT_MODEL = "claude-sonnet-4-5";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();
    private final String apiKey;
    private final String model;

    /**
     * Creates the client using environment configuration.
     */
    public ClaudeLlmClient() {
        this(System.getenv("ANTHROPIC_API_KEY"),
                System.getenv().getOrDefault("ANTHROPIC_MODEL", DEFAULT_MODEL));
    }

    /**
     * Creates the client with explicit credentials (useful for tests).
     *
     * @param apiKey the Anthropic API key (may be null)
     * @param model  the model identifier
     */
    public ClaudeLlmClient(String apiKey, String model) {
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
        return "Claude";
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: POSTs to the Messages API and extracts the first
     * content block's text.</p>
     */
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("max_tokens", 1000);
        body.addProperty("system", systemPrompt);
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", userPrompt);
        List<JsonObject> messages = new ArrayList<>();
        messages.add(message);
        body.add("messages", gson.toJsonTree(messages));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(60))
                .header("x-api-key", apiKey)
                .header("anthropic-version", API_VERSION)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> resp = send(request, "Claude API");
        if (resp.statusCode() != 200) {
            throw new AiEvaluationException(
                    "Claude API returned HTTP " + resp.statusCode());
        }
        try {
            return gson.fromJson(resp.body(), JsonObject.class)
                    .getAsJsonArray("content")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            throw new AiEvaluationException(
                    "Unexpected Claude response shape", e);
        }
    }

    /**
     * Sends an HTTP request, mapping I/O problems to AI exceptions.
     *
     * @param request the request to send
     * @param label   provider label for error messages
     * @return the HTTP response
     */
    protected HttpResponse<String> send(HttpRequest request, String label) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new AiEvaluationException(
                    "Network error calling " + label, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiEvaluationException(
                    "Interrupted while calling " + label, e);
        }
    }
}
