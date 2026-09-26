package com.interviewsim.service;

/**
 * AI answer evaluator backed by the Google Gemini API (free tier).
 * <p>
 * All model communication lives in {@link GeminiLlmClient}; the shared
 * interviewer persona, JSON verdict parsing and keyword statistics come
 * from {@link AbstractLlmEvaluator}.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.1
 */
public class GeminiAnswerEvaluator extends AbstractLlmEvaluator {

    /**
     * Creates a Gemini evaluator using environment-configured credentials.
     */
    public GeminiAnswerEvaluator() {
        super(new GeminiLlmClient());
    }

    /**
     * Creates a Gemini evaluator with an explicit client (useful for tests).
     *
     * @param client the Gemini LLM client
     */
    public GeminiAnswerEvaluator(GeminiLlmClient client) {
        super(client);
    }
}
