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
 * {@link LlmClient} implementation for local open-source models served by
 * <a href="https://ollama.com">Ollama</a> (llama3.1, qwen2.5, phi-4...).
 * <p>
 * Fully local, free and private. Configured via environment variables:
 * {@code OLLAMA_URL} (optional, default {@code http://localhost:11434})
 * and {@code OLLAMA_MODEL} (optional, default {@code llama3.1}).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class OllamaLlmClient implements LlmClient {

    /** Default Ollama server URL. */
    private static final String DEFAULT_URL = "http://localhost:11434";

    /** Default open-source model. */
    private static final String DEFAULT_MODEL = "llama3.1";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private final Gson gson = new Gson();
    private final String baseUrl;
    private final String model;

    /**
     * Creates the client using environment configuration.
     */
    public OllamaLlmClient() {
        this(System.getenv().getOrDefault("OLLAMA_URL", DEFAULT_URL),
                System.getenv().getOrDefault("OLLAMA_MODEL", DEFAULT_MODEL));
    }

    /**
     * Creates the client with an explicit server and model.
     *
     * @param baseUrl the Ollama server base URL
     * @param model   the model tag, e.g. {@code llama3.1}
     */
    public OllamaLlmClient(String baseUrl, String model) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/tags"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> resp =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getProviderName() {
        return "Ollama (" + model + ")";
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: POSTs to the non-streaming {@code /api/chat}
     * endpoint (system+user messages) and extracts {@code message.content}.</p>
     */
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);

        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("stream", false);
        body.add("messages", gson.toJsonTree(new JsonObject[]{systemMessage, userMessage}));
        JsonObject options = new JsonObject();
        options.addProperty("num_predict", 1000);
        options.addProperty("temperature", 0.4);
        body.add("options", options);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .timeout(Duration.ofSeconds(180))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try {
            HttpResponse<String> resp =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new AiEvaluationException(
                        "Ollama returned HTTP " + resp.statusCode());
            }
            return gson.fromJson(resp.body(), JsonObject.class)
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (IOException e) {
            throw new AiEvaluationException(
                    "Network error calling Ollama at " + baseUrl, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiEvaluationException(
                    "Interrupted while calling Ollama", e);
        } catch (AiEvaluationException e) {
            throw e;
        } catch (Exception e) {
            throw new AiEvaluationException(
                    "Unexpected Ollama response shape", e);
        }
    }
}
