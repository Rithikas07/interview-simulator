package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Question setter that picks a fully random, shuffled set of questions
 * from the bank — the classic "surprise mock interview" experience.
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class RandomQuestionSetter implements QuestionSetter {

    private final Random random;

    /**
     * Creates the setter with a default randomness source.
     */
    public RandomQuestionSetter() {
        this(new Random());
    }

    /**
     * Creates the setter with a caller-supplied randomness source
     * (useful for deterministic tests).
     *
     * @param random the {@link Random} instance used for shuffling
     */
    public RandomQuestionSetter(Random random) {
        this.random = random;
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: copies the whole bank, shuffles it, and takes the
     * first {@code count} questions.</p>
     */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, int count) {
        List<Question> copy = new ArrayList<>(bank.getAllQuestions());
        Collections.shuffle(copy, random);
        return copy.subList(0, Math.min(count, copy.size()));
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: filters by topic first, then shuffles.</p>
     */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, Topic topic, int count) {
        List<Question> copy = new ArrayList<>(bank.getQuestionsByTopic(topic));
        Collections.shuffle(copy, random);
        return copy.subList(0, Math.min(count, copy.size()));
    }

    /** {@inheritDoc} */
    @Override
    public String getStrategyName() {
        return "Random";
    }
}
