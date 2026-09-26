package com.interviewsim.service;

import java.util.List;

/**
 * Static factory for {@link AnswerEvaluator} implementations.
 * <p>
 * Demonstrates the "one interface, multiple implementations" idea: callers
 * never instantiate evaluators directly — they ask this factory, which
 * inspects environment configuration and returns the best available
 * evaluator chain:
 * </p>
 * <ul>
 *   <li>{@code static} rule-based evaluator — always available, offline
 *       ({@link KeywordBasedEvaluator})</li>
 *   <li>Claude AI evaluator — cloud LLM ({@link ClaudeAnswerEvaluator})</li>
 *   <li>Gemini AI evaluator — cloud LLM with free tier ({@link GeminiAnswerEvaluator})</li>
 *   <li>Ollama AI evaluator — local open-source models ({@link OllamaAnswerEvaluator})</li>
 * </ul>
 * <p>
 * The factory always wraps the chosen AI evaluator in a
 * {@link FallbackEvaluator} so a session gracefully degrades to the static
 * keyword evaluator on any AI failure.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public final class EvaluatorFactory {

    /** Utility class — not meant to be instantiated. */
    private EvaluatorFactory() {
    }

    /**
     * Returns all AI evaluator implementations the application supports,
     * in priority order (Claude, Gemini, Ollama).
     *
     * @return list of candidate AI evaluators
     */
    public static List<AiAnswerEvaluator> getAiEvaluators() {
        return List.of(new ClaudeAnswerEvaluator(),
                new GeminiAnswerEvaluator(),
                new OllamaAnswerEvaluator());
    }

    /**
     * Returns the static (offline, always-available) evaluator.
     *
     * @return the keyword-based rule evaluator
     */
    public static AnswerEvaluator getStaticEvaluator() {
        return new KeywordBasedEvaluator();
    }

    /**
     * Selects the best available evaluator at call time.
     * <p>
     * Priority: Claude → Gemini → Ollama → static keywords. The chosen AI
     * evaluator (if any) is wrapped in a {@link FallbackEvaluator}.
     * </p>
     *
     * @return the evaluator to use for scoring answers
     */
    public static AnswerEvaluator selectBestEvaluator() {
        for (AiAnswerEvaluator ai : getAiEvaluators()) {
            if (ai.isAvailable()) {
                return new FallbackEvaluator(ai, getStaticEvaluator());
            }
        }
        return getStaticEvaluator();
    }

    /**
     * Returns a human-readable description of the evaluator that
     * {@link #selectBestEvaluator()} would choose — for startup logging.
     *
     * @return a description like "Claude AI (cloud)" or "Static keywords"
     */
    public static String describeSelectedEvaluator() {
        AnswerEvaluator evaluator = selectBestEvaluator();
        if (evaluator instanceof FallbackEvaluator fallback) {
            return describeInner(fallback);
        }
        return "Static keyword evaluator (offline)";
    }

    /**
     * Describes the AI evaluator inside a fallback wrapper.
     *
     * @param fallback the fallback-wrapped evaluator
     * @return the description of the wrapped AI evaluator
     */
    private static String describeInner(FallbackEvaluator fallback) {
        // The wrapped evaluator's class name reveals the provider.
        String simpleName = fallback.getClass().getSimpleName();
        return "AI evaluator (" + simpleName + ") with keyword fallback";
    }
}
