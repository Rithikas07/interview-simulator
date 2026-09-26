package com.interviewsim.service;

/**
 * AI answer evaluator backed by the Anthropic Claude API.
 * <p>
 * All model communication lives in {@link ClaudeLlmClient}; this class only
 * supplies the identity of the backend. The shared interviewer persona,
 * JSON verdict parsing and keyword statistics come from
 * {@link AbstractLlmEvaluator}.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.2
 */
public class ClaudeAnswerEvaluator extends AbstractLlmEvaluator {

    /**
     * Creates a Claude evaluator using environment-configured credentials.
     */
    public ClaudeAnswerEvaluator() {
        super(new ClaudeLlmClient());
    }

    /**
     * Creates a Claude evaluator with an explicit client (useful for tests).
     *
     * @param client the Claude LLM client
     */
    public ClaudeAnswerEvaluator(ClaudeLlmClient client) {
        super(client);
    }
}
