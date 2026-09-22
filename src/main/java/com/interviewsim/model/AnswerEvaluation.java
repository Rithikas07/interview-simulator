package com.interviewsim.model;

/**
 * Holds the AI evaluation result for a single answered question.
 * <p>
 * An immutable value object containing the per-question score, matched and
 * missed keywords, and qualitative feedback.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class AnswerEvaluation {

    private final Question question;
    private final String candidateAnswer;
    private final int score;
    private final java.util.List<String> matchedKeywords;
    private final java.util.List<String> missedKeywords;
    private final String feedback;

    /**
     * Constructs an evaluation result.
     *
     * @param question         the question that was answered
     * @param candidateAnswer  the raw answer typed by the candidate
     * @param score            the score for this answer (0–100)
     * @param matchedKeywords  keywords found in the answer
     * @param missedKeywords   expected keywords absent from the answer
     * @param feedback         qualitative feedback message
     */
    public AnswerEvaluation(Question question, String candidateAnswer, int score,
                            java.util.List<String> matchedKeywords,
                            java.util.List<String> missedKeywords,
                            String feedback) {
        this.question = question;
        this.candidateAnswer = candidateAnswer;
        this.score = score;
        this.matchedKeywords = java.util.List.copyOf(matchedKeywords);
        this.missedKeywords = java.util.List.copyOf(missedKeywords);
        this.feedback = feedback;
    }

    /**
     * Returns the question associated with this evaluation.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Returns the candidate's raw answer text.
     *
     * @return the candidate answer
     */
    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    /**
     * Returns the score awarded for this answer (0–100).
     *
     * @return the score in percent
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the keywords the candidate successfully mentioned.
     *
     * @return matched keywords
     */
    public java.util.List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    /**
     * Returns the expected keywords the candidate missed.
     *
     * @return missed keywords
     */
    public java.util.List<String> getMissedKeywords() {
        return missedKeywords;
    }

    /**
     * Returns qualitative feedback for this answer.
     *
     * @return the feedback message
     */
    public String getFeedback() {
        return feedback;
    }
}
