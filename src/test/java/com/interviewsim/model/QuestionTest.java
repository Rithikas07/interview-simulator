package com.interviewsim.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestionTest {

    @Test
    void gettersReturnConstructorValues() {
        Question q = new Question(1, Topic.OOP, "What is encapsulation?",
                List.of("hiding", "state"), "Encapsulation hides internal state.");

        assertEquals(1, q.getId());
        assertEquals(Topic.OOP, q.getTopic());
        assertEquals("What is encapsulation?", q.getText());
        assertEquals(List.of("hiding", "state"), q.getExpectedKeywords());
        assertEquals("Encapsulation hides internal state.", q.getModelAnswer());
    }

    @Test
    void expectedKeywordsAreUnmodifiable() {
        Question q = new Question(2, Topic.STRINGS, "text",
                List.of("immutable"), "answer");

        assertThrows(UnsupportedOperationException.class,
                () -> q.getExpectedKeywords().add("mutable"));
    }
}
