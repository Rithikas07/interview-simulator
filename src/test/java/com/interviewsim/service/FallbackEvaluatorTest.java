package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FallbackEvaluatorTest {

    private final Question question =
            new Question(1, Topic.EXCEPTIONS, "q", List.of("finally"), "answer");

    /** AI evaluator stub that always throws, to exercise the fallback path. */
    private static class FailingAiEvaluator implements AiAnswerEvaluator {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public AnswerEvaluation evaluate(Question question, String candidateAnswer) {
            throw new RuntimeException("simulated AI outage");
        }
    }

    /** AI evaluator stub that is never available. */
    private static class UnavailableAiEvaluator implements AiAnswerEvaluator {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public AnswerEvaluation evaluate(Question question, String candidateAnswer) {
            throw new RuntimeException("should never be called");
        }
    }

    @Test
    void fallsBackToStaticEvaluatorWhenAiThrows() {
        FallbackEvaluator fallbackEvaluator =
                new FallbackEvaluator(new FailingAiEvaluator(), new KeywordBasedEvaluator());

        assertTrue(fallbackEvaluator.isAiActive());
        AnswerEvaluation eval = fallbackEvaluator.evaluate(question, "handled with finally block");

        assertEquals(100, eval.getScore());
        assertFalse(fallbackEvaluator.isAiActive());
    }

    @Test
    void usesStaticEvaluatorImmediatelyWhenAiUnavailable() {
        FallbackEvaluator fallbackEvaluator =
                new FallbackEvaluator(new UnavailableAiEvaluator(), new KeywordBasedEvaluator());

        assertFalse(fallbackEvaluator.isAiActive());
        AnswerEvaluation eval = fallbackEvaluator.evaluate(question, "finally");

        assertEquals(100, eval.getScore());
    }
}
