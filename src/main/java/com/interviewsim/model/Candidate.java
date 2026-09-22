package com.interviewsim.model;

/**
 * Represents a candidate (university student) using the Interview Simulator.
 * <p>
 * A candidate has a personal profile and tracks placement-preparation
 * statistics such as the best score achieved and the number of interviews
 * completed. This class follows the encapsulation principle: all fields are
 * private and accessed through getters and setters.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class Candidate {

    /** Sentinel value indicating the candidate has never completed an interview. */
    private static final int NO_SCORE_YET = -1;

    private final String name;
    private final String university;
    private final String rollNumber;

    private int bestScorePercentage;
    private int interviewsCompleted;

    /**
     * Constructs a new candidate profile.
     *
     * @param name        the full name of the candidate
     * @param university   the university / college of the candidate
     * @param rollNumber   the unique roll or registration number
     */
    public Candidate(String name, String university, String rollNumber) {
        this.name = name;
        this.university = university;
        this.rollNumber = rollNumber;
        this.bestScorePercentage = NO_SCORE_YET;
        this.interviewsCompleted = 0;
    }

    /**
     * Returns the candidate's full name.
     *
     * @return the candidate name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the candidate's university.
     *
     * @return the university name
     */
    public String getUniversity() {
        return university;
    }

    /**
     * Returns the candidate's roll number.
     *
     * @return the roll number
     */
    public String getRollNumber() {
        return rollNumber;
    }

    /**
     * Returns the best score (percentage) achieved across all interviews,
     * or {@code -1} if no interview has been completed yet.
     *
     * @return the best score percentage, or {@code -1} if none yet
     */
    public int getBestScorePercentage() {
        return bestScorePercentage;
    }

    /**
     * Returns the total number of interviews completed by this candidate.
     *
     * @return the count of completed interviews
     */
    public int getInterviewsCompleted() {
        return interviewsCompleted;
    }

    /**
     * Records the result of one completed interview session.
     * <p>
     * Increments the completed-interview counter and updates the best score
     * if the given score is higher than the previous best (or if this is the
     * first interview).
     * </p>
     *
     * @param scorePercentage the score of the just-finished interview, in percent
     */
    public void recordInterviewResult(int scorePercentage) {
        interviewsCompleted++;
        if (bestScorePercentage == NO_SCORE_YET || scorePercentage > bestScorePercentage) {
            bestScorePercentage = scorePercentage;
        }
    }

    /**
     * Returns a human-readable one-line summary of the candidate profile.
     *
     * @return a profile summary string
     */
    @Override
    public String toString() {
        return String.format("%s (%s, %s) | Interviews completed: %d | Best score: %s",
                name, rollNumber, university, interviewsCompleted,
                bestScorePercentage == NO_SCORE_YET ? "N/A" : bestScorePercentage + "%");
    }
}
