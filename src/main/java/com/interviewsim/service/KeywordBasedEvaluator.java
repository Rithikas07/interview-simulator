package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based implementation of {@link AnswerEvaluator}.
 * <p>
 * The "AI" evaluation strategy works as follows: the candidate's answer is
 * lower-cased and searched for each expected keyword of the question. The
 * score is the percentage of keywords matched, and feedback is generated
 * from the match count, the missed keywords and the answer length.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class KeywordBasedEvaluator implements AnswerEvaluator {

    private static final int GOOD_ANSWER_THRESHOLD = 75;
    private static final int OK_ANSWER_THRESHOLD = 40;

    /** {@inheritDoc} */
    @Override
    public AnswerEvaluation evaluate(Question question, String candidateAnswer) {
        List<String> matched = new ArrayList<>();
        List<String> missed = new ArrayList<>();

        String normalized = candidateAnswer == null ? "" : candidateAnswer.toLowerCase();

        for (String keyword : question.getExpectedKeywords()) {
            if (normalized.contains(keyword.toLowerCase())) {
                matched.add(keyword);
            } else {
                missed.add(keyword);
            }
        }

        int total = matched.size() + missed.size();
        int score = total == 0 ? 0 : (int) Math.round(matched.size() * 100.0 / total);

        String feedback = buildFeedback(score, matched.size(), total, candidateAnswer);

        return new AnswerEvaluation(question, candidateAnswer, score, matched, missed, feedback);
    }

    /**
     * Produces qualitative feedback based on the score and answer length.
     *
     * @param score         the computed score (0–100)
     * @param matchedCount  number of matched keywords
     * @param totalCount    total expected keywords
     * @param answer        the raw candidate answer (may be null/empty)
     * @return a feedback message
     */
    private String buildFeedback(int score, int matchedCount, int totalCount, String answer) {
        if (answer == null || answer.isBlank()) {
            return "No answer given. Try to attempt every question — interviewers value structure and keywords.";
        }
        if (score >= GOOD_ANSWER_THRESHOLD) {
            return String.format("Excellent! You covered %d/%d key points. A strong placement-level answer.",
                    matchedCount, totalCount);
        }
        if (score >= OK_ANSWER_THRESHOLD) {
            return String.format("Partially correct — covered %d/%d key points. Add the missing concepts for full marks.",
                    matchedCount, totalCount);
        }
        return String.format("Weak answer — only %d/%d key points covered. Revise this topic before your next attempt.",
                matchedCount, totalCount);
    }
}
