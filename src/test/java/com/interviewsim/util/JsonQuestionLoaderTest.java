package com.interviewsim.util;

import com.interviewsim.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonQuestionLoaderTest {

    @Test
    void loadDefaultReturnsAllQuestionsFromResourceFile() {
        List<Question> questions = new JsonQuestionLoader().loadDefault();

        assertEquals(23, questions.size());
        assertTrue(questions.stream().allMatch(q -> q.getTopic() != null));
    }

    @Test
    void loadUnknownResourceReturnsEmptyListInsteadOfThrowing() {
        List<Question> questions = new JsonQuestionLoader().load("/does-not-exist.json");

        assertFalse(questions == null);
        assertEquals(0, questions.size());
    }
}
