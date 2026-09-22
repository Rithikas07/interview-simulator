package com.interviewsim.core;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Question;
import com.interviewsim.service.AnswerEvaluator;
import com.interviewsim.service.QuestionBank;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates one interview session.
 * <p>
 * The engine pulls random questions from the {@link QuestionBank}, hands
 * each one to an {@link InterviewListener} (the console UI) for display and
 * answer capture, evaluates each answer through the {@link AnswerEvaluator},
 * and collects the evaluations used for the final report. It is deliberately
 * UI-agnostic via the observer-style {@link InterviewListener} interface.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class InterviewSession {

    /** Default number of questions per session. */
    public static final int DEFAULT_QUESTION_COUNT = 5;

    private final QuestionBank questionBank;
    private final AnswerEvaluator evaluator;
    private final Candidate candidate;
    private final int questionCount;

    /**
     * Creates a session using the default question count.
     *
     * @param questionBank source of questions
     * @param evaluator    answer evaluation strategy
     * @param candidate    the candidate being interviewed
     */
    public InterviewSession(QuestionBank questionBank, AnswerEvaluator evaluator, Candidate candidate) {
        this(questionBank, evaluator, candidate, DEFAULT_QUESTION_COUNT);
    }

    /**
     * Creates a session with a custom question count.
     *
     * @param questionBank  source of questions
     * @param evaluator     answer evaluation strategy
     * @param candidate     the candidate being interviewed
     * @param questionCount number of questions in this session
     */
    public InterviewSession(QuestionBank questionBank, AnswerEvaluator evaluator,
                            Candidate candidate, int questionCount) {
        this.questionBank = questionBank;
        this.evaluator = evaluator;
        this.candidate = candidate;
        this.questionCount = questionCount;
    }

    /**
     * Runs the full interview session with the given listener as the UI.
     *
     * @param listener the UI listener that shows questions and collects answers
     * @return the evaluations of all answers in this session
     */
    public List<AnswerEvaluation> run(InterviewListener listener) {
        List<Question> questions = questionBank.getRandomQuestions(questionCount);
        List<AnswerEvaluation> evaluations = new ArrayList<>();

        listener.onSessionStart(candidate, questions.size());

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String answer = listener.onAskQuestion(i + 1, questions.size(), q);
            AnswerEvaluation evaluation = evaluator.evaluate(q, answer);
            listener.onAnswerEvaluated(evaluation);
            evaluations.add(evaluation);
        }

        int finalScore = averageScore(evaluations);
        candidate.recordInterviewResult(finalScore);
        listener.onSessionEnd(evaluations, finalScore);

        return evaluations;
    }

    /**
     * Computes the average score of a set of evaluations.
     *
     * @param evaluations the evaluations to average
     * @return the average percentage score
     */
    private int averageScore(List<AnswerEvaluation> evaluations) {
        if (evaluations.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (AnswerEvaluation e : evaluations) {
            sum += e.getScore();
        }
        return (int) Math.round(sum / evaluations.size());
    }
}
