package com.interviewsim.service;

/**
 * RuntimeException thrown when an AI-backed operation (answer evaluation,
 * question generation) cannot be completed.
 * <p>
 * Callers — typically {@link FallbackEvaluator} — catch this exception to
 * gracefully degrade to the offline (keyword-based) implementation.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class AiEvaluationException extends RuntimeException {

    /**
     * Creates the exception with a message.
     *
     * @param message the failure description
     */
    public AiEvaluationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and cause.
     *
     * @param message the failure description
     * @param cause   the underlying exception
     */
    public AiEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
