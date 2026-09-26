package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;

/**
 * Decorator around an {@link AiAnswerEvaluator} adding automatic fallback
 * to a secondary (rule-based) evaluator when the AI backend is unavailable
 * or fails mid-interview (missing API key, network error, bad response...).
 * <p>
 * This keeps the interview session resilient: candidates are never blocked
 * by infrastructure problems, and the UI gives a small notice when scoring
 * degrades to the offline evaluator.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class FallbackEvaluator implements AnswerEvaluator {

    private final AiAnswerEvaluator primary;
    private final AnswerEvaluator fallback;
    private boolean aiActive;

    /**
     * Creates a fallback-aware evaluator.
     *
     * @param primary  the AI evaluator tried first
     * @param fallback the offline evaluator used when the AI fails
     */
    public FallbackEvaluator(AiAnswerEvaluator primary, AnswerEvaluator fallback) {
        this.primary = primary;
        this.fallback = fallback;
        this.aiActive = primary.isAvailable();
    }

    /**
     * Returns whether the AI evaluator is currently in use.
     * <p>Becomes {@code false} after the first AI failure, until a new
     * instance is created.</p>
     *
     * @return {@code true} if AI evaluations are active
     */
    public boolean isAiActive() {
        return aiActive;
    }

    /**
     * Evaluates using the AI evaluator, transparently falling back to the
     * rule-based evaluator on any AI failure.
     *
     * @param question        the question being answered
     * @param candidateAnswer the candidate's raw answer text
     * @return the evaluation, from AI if possible, otherwise from the fallback
     */
    @Override
    public AnswerEvaluation evaluate(Question question, String candidateAnswer) {
        if (aiActive) {
            try {
                return primary.evaluate(question, candidateAnswer);
            } catch (RuntimeException e) {
                aiActive = false;
                System.err.println("[INFO] AI evaluator unavailable ("
                        + e.getMessage() + "). Falling back to keyword-based scoring.");
            }
        }
        return fallback.evaluate(question, candidateAnswer);
    }
}
