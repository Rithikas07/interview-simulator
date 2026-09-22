package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;

/**
 * Contract for the AI answer evaluator.
 * <p>
 * The "AI" in this application is rule-based: it compares the candidate's
 * answer against the expected keywords of a question and produces a score
 * with feedback. Implementations may refine the scoring strategy without
 * affecting the rest of the application (Strategy pattern).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface AnswerEvaluator {

    /**
     * Evaluates a candidate's answer to a question.
     *
     * @param question         the question being answered
     * @param candidateAnswer the raw answer text typed by the candidate
     * @return an {@link AnswerEvaluation} containing score and feedback
     */
    AnswerEvaluation evaluate(Question question, String candidateAnswer);
}
