package com.interviewsim.service;

import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeywordBasedEvaluatorTest {

    private final KeywordBasedEvaluator evaluator = new KeywordBasedEvaluator();

    private Question questionWithKeywords(String... keywords) {
        return new Question(1, Topic.OOP, "sample question", List.of(keywords), "model answer");
    }

    @Test
    void fullKeywordMatchScoresHundred() {
        Question q = questionWithKeywords("encapsulation", "inheritance");

        AnswerEvaluation eval = evaluator.evaluate(q, "Encapsulation and Inheritance are both pillars.");

        assertEquals(100, eval.getScore());
        assertEquals(2, eval.getMatchedKeywords().size());
        assertTrue(eval.getMissedKeywords().isEmpty());
    }

    @Test
    void partialKeywordMatchScoresProportionally() {
        Question q = questionWithKeywords("encapsulation", "inheritance", "polymorphism", "abstraction");

        AnswerEvaluation eval = evaluator.evaluate(q, "Encapsulation hides fields.");

        assertEquals(25, eval.getScore());
        assertEquals(1, eval.getMatchedKeywords().size());
        assertEquals(3, eval.getMissedKeywords().size());
    }

    @Test
    void blankAnswerScoresZeroWithNoAnswerFeedback() {
        Question q = questionWithKeywords("keyword");

        AnswerEvaluation eval = evaluator.evaluate(q, "   ");

        assertEquals(0, eval.getScore());
        assertTrue(eval.getFeedback().toLowerCase().contains("no answer"));
    }

    @Test
    void nullAnswerIsTreatedAsNoMatches() {
        Question q = questionWithKeywords("keyword");

        AnswerEvaluation eval = evaluator.evaluate(q, null);

        assertEquals(0, eval.getScore());
        assertEquals(1, eval.getMissedKeywords().size());
    }

    @Test
    void keywordMatchingIsCaseInsensitive() {
        Question q = questionWithKeywords("HashMap");

        AnswerEvaluation eval = evaluator.evaluate(q, "I used a hashmap for O(1) lookups.");

        assertEquals(100, eval.getScore());
    }
}
