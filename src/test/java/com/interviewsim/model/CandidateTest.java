package com.interviewsim.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandidateTest {

    @Test
    void newCandidateHasNoScoreYet() {
        Candidate c = new Candidate("Asha", "Anna University", "R001");

        assertEquals(-1, c.getBestScorePercentage());
        assertEquals(0, c.getInterviewsCompleted());
        assertTrue(c.toString().contains("N/A"));
    }

    @Test
    void recordInterviewResultTracksBestScoreAndCount() {
        Candidate c = new Candidate("Ravi", "CIT", "R002");

        c.recordInterviewResult(60);
        c.recordInterviewResult(85);
        c.recordInterviewResult(40);

        assertEquals(3, c.getInterviewsCompleted());
        assertEquals(85, c.getBestScorePercentage());
    }

    @Test
    void toStringIncludesLatestBestScore() {
        Candidate c = new Candidate("Meera", "CIT", "R003");
        c.recordInterviewResult(72);

        assertTrue(c.toString().contains("72%"));
        assertTrue(c.toString().contains("Meera"));
    }
}
