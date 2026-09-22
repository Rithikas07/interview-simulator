package com.interviewsim.ui;

import com.interviewsim.core.InterviewListener;
import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Question;

import java.util.List;
import java.util.Scanner;

/**
 * Console implementation of {@link InterviewListener}.
 * <p>
 * Renders the interview to {@code System.out}, reads answers from
 * {@code System.in} via a {@link Scanner}, and prints per-question score,
 * feedback and model answers immediately after evaluation.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class ConsoleInterviewUI implements InterviewListener {

    private final Scanner scanner;

    /**
     * Constructs the console UI with a shared scanner (closed by the owner,
     * typically the main application, to avoid double-closing System.in).
     *
     * @param scanner the scanner used for all user input
     */
    public ConsoleInterviewUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /** {@inheritDoc} */
    @Override
    public void onSessionStart(Candidate candidate, int questionCount) {
        System.out.println();
        System.out.println("****************************************************************");
        System.out.println("  INTERVIEW SESSION STARTING");
        System.out.println("****************************************************************");
        System.out.println("Candidate : " + candidate.getName());
        System.out.println("Questions : " + questionCount);
        System.out.println("Read each question carefully and answer in 2-4 lines.");
        System.out.println("Tip: interviewers look for the key technical terms in your answer.");
        System.out.println("****************************************************************");
    }

    /** {@inheritDoc} */
    @Override
    public String onAskQuestion(int questionNumber, int totalQuestions, Question question) {
        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Question %d of %d  [%s]%n",
                questionNumber, totalQuestions, question.getTopic().getDisplayName());
        System.out.println("----------------------------------------------------------------");
        System.out.println(question.getText());
        System.out.print("Your answer > ");
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }

    /** {@inheritDoc} */
    @Override
    public void onAnswerEvaluated(AnswerEvaluation evaluation) {
        System.out.println();
        System.out.println("  Score        : " + evaluation.getScore() + "/100");
        System.out.println("  Feedback     : " + evaluation.getFeedback());
        if (!evaluation.getMatchedKeywords().isEmpty()) {
            System.out.println("  Hit keywords : " + String.join(", ", evaluation.getMatchedKeywords()));
        }
        if (!evaluation.getMissedKeywords().isEmpty()) {
            System.out.println("  Missed       : " + String.join(", ", evaluation.getMissedKeywords()));
        }
        System.out.println("  Model answer : " + evaluation.getQuestion().getModelAnswer());
        System.out.println("----------------------------------------------------------------");
    }

    /** {@inheritDoc} */
    @Override
    public void onSessionEnd(List<AnswerEvaluation> evaluations, int finalScore) {
        System.out.println();
        System.out.println("Interview complete! You scored " + finalScore + "% this session.");
        System.out.println("Generating your placement-readiness report...");
    }
}
