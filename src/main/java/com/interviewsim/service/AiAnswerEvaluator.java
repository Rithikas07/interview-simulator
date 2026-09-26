package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;
import java.util.List;

/**
 * Extension of {@link AnswerEvaluator} that provides intelligent,
 * AI-style feedback beyond simple keyword counting.
 * <p>
 * Implemented by evaluators that use a large language model (or a
 * similarly advanced technique) to assess answer correctness, depth and
 * communication quality.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface AiAnswerEvaluator extends AnswerEvaluator {

    /**
     * Checks whether the AI backend is currently available and usable
     * (e.g. API key configured, network reachable, model running).
     *
     * @return {@code true} if the AI evaluation can be performed
     */
    boolean isAvailable();
}
