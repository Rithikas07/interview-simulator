package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryQuestionBankTest {

    @Test
    void loadsQuestionsFromClasspathJsonOnConstruction() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(42));

        assertFalse(bank.getAllQuestions().isEmpty());
    }

    @Test
    void getQuestionsByTopicFiltersCorrectly() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(42));

        List<Question> oopQuestions = bank.getQuestionsByTopic(Topic.OOP);

        assertFalse(oopQuestions.isEmpty());
        assertTrue(oopQuestions.stream().allMatch(q -> q.getTopic() == Topic.OOP));
    }

    @Test
    void getRandomQuestionsNeverExceedsRequestedCount() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(1));

        List<Question> selected = bank.getRandomQuestions(3);

        assertEquals(3, selected.size());
    }

    @Test
    void getRandomQuestionsCapsAtBankSizeWhenCountIsTooLarge() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(1));
        int bankSize = bank.getAllQuestions().size();

        List<Question> selected = bank.getRandomQuestions(bankSize + 100);

        assertEquals(bankSize, selected.size());
    }

    @Test
    void addQuestionsAppendsToTheBank() {
        QuestionBank bank = new InMemoryQuestionBank(new Random(1));
        int before = bank.getAllQuestions().size();
        Question extra = new Question(9001, Topic.JVM, "extra question",
                List.of("heap"), "model answer");

        bank.addQuestions(List.of(extra));

        assertEquals(before + 1, bank.getAllQuestions().size());
    }
}
