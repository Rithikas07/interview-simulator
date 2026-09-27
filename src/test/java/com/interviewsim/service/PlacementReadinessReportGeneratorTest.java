package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementReadinessReportGeneratorTest {

    private AnswerEvaluation evaluationWithScore(Topic topic, int score) {
        Question q = new Question(1, topic, "q", List.of("k"), "a");
        return new AnswerEvaluation(q, "answer", score, List.of("k"), List.of(), "feedback");
    }

    @Test
    void highAverageScoreProducesReadyVerdict() {
        Candidate candidate = new Candidate("Diya", "CIT", "R010");
        ReportGenerator generator = new PlacementReadinessReportGenerator(candidate);
        List<AnswerEvaluation> evaluations = List.of(
                evaluationWithScore(Topic.OOP, 90),
                evaluationWithScore(Topic.COLLECTIONS, 80));

        String report = generator.generateReport(candidate, evaluations);

        assertTrue(report.contains("READY"));
        assertTrue(report.contains("Diya"));
    }

    @Test
    void lowAverageScoreProducesNeedsPreparationVerdict() {
        Candidate candidate = new Candidate("Karan", "CIT", "R011");
        ReportGenerator generator = new PlacementReadinessReportGenerator(candidate);
        List<AnswerEvaluation> evaluations = List.of(
                evaluationWithScore(Topic.EXCEPTIONS, 10),
                evaluationWithScore(Topic.JVM, 20));

        String report = generator.generateReport(candidate, evaluations);

        assertTrue(report.contains("NEEDS PREPARATION"));
    }

    @Test
    void midRangeAverageScoreProducesAlmostThereVerdict() {
        Candidate candidate = new Candidate("Nila", "CIT", "R012");
        ReportGenerator generator = new PlacementReadinessReportGenerator(candidate);
        List<AnswerEvaluation> evaluations = List.of(
                evaluationWithScore(Topic.STRINGS, 60),
                evaluationWithScore(Topic.MULTITHREADING, 55));

        String report = generator.generateReport(candidate, evaluations);

        assertTrue(report.contains("ALMOST THERE"));
    }
}
