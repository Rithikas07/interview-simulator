package com.interviewsim.core;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Question;

import java.util.List;

/**
 * Observer-style callback interface used by {@link InterviewSession} to
 * interact with the user during an interview.
 * <p>
 * Separating the UI contract from the engine keeps the core logic testable
 * and allows different front-ends (console, GUI, web) without changing the
 * engine.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface InterviewListener {

    /**
     * Called once when the interview session starts.
     *
     * @param candidate      the candidate being interviewed
     * @param questionCount  total number of questions in the session
     */
    void onSessionStart(Candidate candidate, int questionCount);

    /**
     * Called for each question; must display it and return the candidate's answer.
     *
     * @param questionNumber 1-based index of the current question
     * @param totalQuestions total number of questions in the session
     * @param question       the question to present
     * @return the candidate's raw answer text (may be empty if skipped)
     */
    String onAskQuestion(int questionNumber, int totalQuestions, Question question);

    /**
     * Called immediately after each answer is evaluated, so per-question
     * score, feedback and the model answer can be shown.
     *
     * @param evaluation the evaluation result for the last answer
     */
    void onAnswerEvaluated(AnswerEvaluation evaluation);

    /**
     * Called once when the session ends.
     *
     * @param evaluations all evaluations of the session, in order
     * @param finalScore  the final averaged score percentage
     */
    void onSessionEnd(List<AnswerEvaluation> evaluations, int finalScore);
}
