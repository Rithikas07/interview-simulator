package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomQuestionSetterTest {

    @Test
    void selectQuestionsReturnsRequestedCount() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(5));
        RandomQuestionSetter setter = new RandomQuestionSetter(new Random(5));

        List<Question> selected = setter.selectQuestions(bank, 4);

        assertEquals(4, selected.size());
    }

    @Test
    void selectQuestionsByTopicOnlyReturnsThatTopic() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(5));
        RandomQuestionSetter setter = new RandomQuestionSetter(new Random(5));

        List<Question> selected = setter.selectQuestions(bank, Topic.MULTITHREADING, 2);

        assertTrue(selected.stream().allMatch(q -> q.getTopic() == Topic.MULTITHREADING));
    }

    @Test
    void strategyNameIsRandom() {
        assertEquals("Random", new RandomQuestionSetter().getStrategyName());
    }
}
