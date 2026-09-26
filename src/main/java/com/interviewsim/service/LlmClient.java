package com.interviewsim.service;

/**
 * Contract for a chat-style large language model (LLM) backend.
 * <p>
 * One interface, multiple implementations — each provider (Anthropic
 * Claude, Google Gemini, local Ollama models) gets its own implementation
 * class. Any feature needing AI (answer evaluation, question generation)
 * programs against this interface only, never against a concrete provider.
 * </p>
 *
 * <p>Known implementations:</p>
 * <ul>
 *   <li>{@link ClaudeLlmClient} — Anthropic Messages API (ANTHROPIC_API_KEY)</li>
 *   <li>{@link GeminiLlmClient} — Google Gemini API, free tier (GEMINI_API_KEY)</li>
 *   <li>{@link OllamaLlmClient} — local open-source models (Ollama)</li>
 * </ul>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface LlmClient {

    /**
     * Checks whether this backend is currently usable (API key present,
     * service reachable).
     *
     * @return {@code true} if chat calls are expected to succeed
     */
    boolean isAvailable();

    /**
     * Sends a system prompt plus user prompt to the model and returns
     * its text reply.
     *
     * @param systemPrompt the persona/instruction prompt
     * @param userPrompt   the actual question/request for the model
     * @return the model's raw text response
     * @throws AiEvaluationException on any failure
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * Returns a human-readable provider name (e.g. "Claude") used in
     * startup messages and menus.
     *
     * @return the provider display name
     */
    String getProviderName();
}
