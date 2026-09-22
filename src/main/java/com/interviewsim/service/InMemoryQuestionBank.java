package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import com.interviewsim.util.JsonQuestionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * In-memory implementation of {@link QuestionBank}.
 * <p>
 * Loads its questions from the external JSON file ({@code questions.json}
 * on the classpath) via {@link JsonQuestionLoader}, then supports random
 * selection using {@link Collections#shuffle} backed by a {@link Random}
 * source. Because the questions live in an external file, they can be
 * edited without recompiling the application.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.2
 */
public class InMemoryQuestionBank implements QuestionBank {

    private final List<Question> questions = new ArrayList<>();
    private final Random random;

    /**
     * Constructs the bank with a default source of randomness.
     */
    public InMemoryQuestionBank() {
        this(new Random());
    }

    /**
     * Constructs the bank with a caller-supplied randomness source.
     * <p>
     * The question set is not defined here; it is copied at construction
     * time from the static {@link StaticQuestionData} catalogue, so all
     * banks share a single source of truth for questions.
     * </p>
     *
     * @param random the {@link Random} instance used for shuffling
     */
    public InMemoryQuestionBank(Random random) {
        this.random = random;
        List<Question> loaded = new JsonQuestionLoader().loadDefault();
        this.questions.addAll(loaded);
    }

    /**
     * Returns all questions available in the bank.
     *
     * @return a defensive copy of all questions
     */
    @Override
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * Returns a random selection of questions for one interview session.
     *
     * @param count the number of random questions to return
     * @return a randomly ordered list of questions
     */
    @Override
    public List<Question> getRandomQuestions(int count) {
        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy, random);
        return new ArrayList<>(copy.subList(0, Math.min(count, copy.size())));
    }

    /**
     * Returns all questions belonging to a given topic.
     *
     * @param topic the topic to filter by
     * @return a list of questions in that topic
     */
    @Override
    public List<Question> getQuestionsByTopic(Topic topic) {
        List<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (q.getTopic() == topic) {
                result.add(q);
            }
        }
        return result;
    }
}
