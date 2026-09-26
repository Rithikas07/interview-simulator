package com.interviewsim.service;

/**
 * AI answer evaluator backed by local open-source models via Ollama.
 * <p>
 * All model communication lives in {@link OllamaLlmClient}; the shared
 * interviewer persona, JSON verdict parsing and keyword statistics come
 * from {@link AbstractLlmEvaluator}.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.1
 */
public class OllamaAnswerEvaluator extends AbstractLlmEvaluator {

    /**
     * Creates an Ollama evaluator using environment-configured server/model.
     */
    public OllamaAnswerEvaluator() {
        super(new OllamaLlmClient());
    }

    /**
     * Creates an Ollama evaluator with an explicit client (useful for tests).
     *
     * @param client the Ollama LLM client
     */
    public OllamaAnswerEvaluator(OllamaLlmClient client) {
        super(client);
    }
}
