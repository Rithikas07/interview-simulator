package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopicQuestionSetterTest {

    @Test
    void onlyReturnsQuestionsFromBoundTopic() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(3));
        TopicQuestionSetter setter = new TopicQuestionSetter(Topic.JVM);

        List<Question> selected = setter.selectQuestions(bank, 5);

        assertTrue(selected.stream().allMatch(q -> q.getTopic() == Topic.JVM));
    }

    @Test
    void defaultConstructorBindsToOopTopic() {
        assertEquals(Topic.OOP, new TopicQuestionSetter().getTopic());
    }

    @Test
    void strategyNameIncludesTopicDisplayName() {
        TopicQuestionSetter setter = new TopicQuestionSetter(Topic.STRINGS);

        assertEquals("Topic focus: Strings", setter.getStrategyName());
    }
}
