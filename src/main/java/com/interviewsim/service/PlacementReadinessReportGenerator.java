package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Text-based implementation of {@link ReportGenerator}.
 * <p>
 * Produces the final placement-readiness report: overall score, per-topic
 * breakdown, strengths, weaknesses and a readiness verdict, plus the
 * candidate's historical statistics (best score, completed interviews).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class PlacementReadinessReportGenerator implements ReportGenerator {

    private static final int READY_THRESHOLD = 75;
    private static final int ALMOST_THRESHOLD = 50;

    private final Candidate candidate;

    /**
     * Constructs a report generator bound to the given candidate.
     *
     * @param candidate the candidate whose session is being reported
     */
    public PlacementReadinessReportGenerator(Candidate candidate) {
        this.candidate = candidate;
    }

    /** {@inheritDoc} */
    @Override
    public String generateReport(Candidate candidate, List<AnswerEvaluation> evaluations) {
        StringBuilder sb = new StringBuilder();

        appendHeader(sb, candidate);
        appendOverallScore(sb, evaluations);

        Map<Topic, int[]> perTopic = computeTopicStats(evaluations);
        appendTopicBreakdown(sb, perTopic);

        appendVerdict(sb, averageScore(evaluations));
        appendHistory(sb, candidate);

        return sb.toString();
    }

    /**
     * Writes the report header (title and candidate profile).
     *
     * @param sb        the builder to append to
     * @param candidate the candidate being reported on
     */
    private void appendHeader(StringBuilder sb, Candidate candidate) {
        sb.append("================================================================\n");
        sb.append("              FINAL PLACEMENT-READINESS REPORT\n");
        sb.append("================================================================\n");
        sb.append("Candidate : ").append(candidate.getName()).append('\n');
        sb.append("University : ").append(candidate.getUniversity()).append('\n');
        sb.append("Roll No.  : ").append(candidate.getRollNumber()).append('\n');
        sb.append("----------------------------------------------------------------\n");
    }

    /**
     * Writes the overall session score line.
     *
     * @param sb          the builder to append to
     * @param evaluations the session evaluations
     */
    private void appendOverallScore(StringBuilder sb, List<AnswerEvaluation> evaluations) {
        sb.append(String.format("Overall score this session : %d/%d (%d%%)%n",
                (int) totalScore(evaluations), evaluations.size(), averageScore(evaluations)));
        sb.append("----------------------------------------------------------------\n");
    }

    /**
     * Writes the per-topic performance breakdown.
     *
     * @param sb        the builder to append to
     * @param perTopic  map of topic -> {scoreSum, count}
     */
    private void appendTopicBreakdown(StringBuilder sb, Map<Topic, int[]> perTopic) {
        sb.append("Topic-wise performance:\n");
        for (Map.Entry<Topic, int[]> entry : perTopic.entrySet()) {
            int[] stats = entry.getValue();
            int avg = (int) Math.round(stats[0] / (double) stats[1]);
            sb.append(String.format("  %-16s : %d%%  (%s)%n",
                    entry.getKey().getDisplayName(), avg, bar(avg)));
        }
        sb.append("----------------------------------------------------------------\n");
    }

    /**
     * Writes the readiness verdict based on the average score.
     *
     * @param sb     the builder to append to
     * @param avgPct the average session score percentage
     */
    private void appendVerdict(StringBuilder sb, int avgPct) {
        String verdict;
        if (avgPct >= READY_THRESHOLD) {
            verdict = "READY — strong command of core Java. You are prepared for technical rounds!";
        } else if (avgPct >= ALMOST_THRESHOLD) {
            verdict = "ALMOST THERE — solid base, but strengthen the weaker topics listed above.";
        } else {
            verdict = "NEEDS PREPARATION — focus on the weak topics above and practise daily.";
        }
        sb.append("Verdict : ").append(verdict).append('\n');
        sb.append("----------------------------------------------------------------\n");
    }

    /**
     * Writes the candidate's historical statistics.
     *
     * @param sb        the builder to append to
     * @param candidate the candidate being reported on
     */
    private void appendHistory(StringBuilder sb, Candidate candidate) {
        sb.append("Interviews completed  : ").append(candidate.getInterviewsCompleted()).append('\n');
        sb.append("Best score so far     : ")
          .append(candidate.getBestScorePercentage() < 0 ? "N/A" : candidate.getBestScorePercentage() + "%")
          .append('\n');
        sb.append("================================================================\n");
    }

    /**
     * Computes per-topic score aggregates from evaluations.
     *
     * @param evaluations the session evaluations
     * @return map of topic to {@code [scoreSum, count]}
     */
    private Map<Topic, int[]> computeTopicStats(List<AnswerEvaluation> evaluations) {
        Map<Topic, int[]> perTopic = new HashMap<>();
        for (AnswerEvaluation e : evaluations) {
            Topic topic = e.getQuestion().getTopic();
            int[] stats = perTopic.computeIfAbsent(topic, t -> new int[2]);
            stats[0] += e.getScore();
            stats[1]++;
        }
        return perTopic;
    }

    /**
     * Returns the total score across all evaluations.
     *
     * @param evaluations the evaluations to sum
     * @return the sum of individual scores
     */
    private double totalScore(List<AnswerEvaluation> evaluations) {
        double sum = 0;
        for (AnswerEvaluation e : evaluations) {
            sum += e.getScore();
        }
        return sum;
    }

    /**
     * Returns the average score percentage of the session.
     *
     * @param evaluations the evaluations to average
     * @return average score in percent (0 if no evaluations)
     */
    private int averageScore(List<AnswerEvaluation> evaluations) {
        if (evaluations.isEmpty()) {
            return 0;
        }
        return (int) Math.round(totalScore(evaluations) / evaluations.size());
    }

    /**
     * Renders a simple 10-character progress bar for a percentage.
     *
     * @param pct the percentage (0–100)
     * @return a bar such as "######----"
     */
    private String bar(int pct) {
        int filled = Math.round(pct / 10f);
        return "#".repeat(filled) + "-".repeat(10 - filled);
    }
}
