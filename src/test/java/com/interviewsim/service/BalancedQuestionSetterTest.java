package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BalancedQuestionSetterTest {

    @Test
    void distributesOneQuestionPerTopicBeforeRepeating() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(7));
        BalancedQuestionSetter setter = new BalancedQuestionSetter();

        List<Question> selected = setter.selectQuestions(bank, Topic.values().length);
        long distinctTopics = selected.stream().map(Question::getTopic).distinct().count();

        assertEquals(Topic.values().length, selected.size());
        assertEquals(Topic.values().length, distinctTopics);
    }

    @Test
    void topicScopedSelectionDelegatesToSingleTopic() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(7));
        BalancedQuestionSetter setter = new BalancedQuestionSetter();

        List<Question> selected = setter.selectQuestions(bank, Topic.COLLECTIONS, 2);

        assertEquals(2, selected.stream().filter(q -> q.getTopic() == Topic.COLLECTIONS)
                .collect(Collectors.toList()).size());
    }

    @Test
    void strategyNameMentionsAllTopics() {
        assertEquals("Balanced (all topics)", new BalancedQuestionSetter().getStrategyName());
    }
}
