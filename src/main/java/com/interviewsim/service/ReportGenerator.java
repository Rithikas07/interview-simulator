package com.interviewsim.service;

import com.interviewsim.model.Candidate;
import java.util.List;
import com.interviewsim.model.AnswerEvaluation;

/**
 * Contract for generating a placement-readiness report.
 * <p>
 * Implementations take a candidate and the evaluations of one completed
 * interview session and produce a full report including per-topic
 * performance, final score and placement-readiness verdict.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface ReportGenerator {

    /**
     * Generates the placement-readiness report for one interview session.
     *
     * @param candidate   the candidate who was interviewed
     * @param evaluations the evaluations of all answers in the session
     * @return the formatted report text
     */
    String generateReport(Candidate candidate, List<AnswerEvaluation> evaluations);
}
