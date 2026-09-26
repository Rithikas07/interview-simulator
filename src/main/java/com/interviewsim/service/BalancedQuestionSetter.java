package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Question setter that guarantees <em>balanced</em> coverage — questions
 * are drawn round-robin across all topics so every session touches as many
 * areas as possible (like a real placement interview).
 * <p>
 * Example: 5 questions across 6 topics → one question each from five
 * different topics. Duplicates from the same topic are only used when the
 * requested count exceeds one-per-topic.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class BalancedQuestionSetter implements QuestionSetter {

    /** Delegate used for randomizing within each topic's questions. */
    private final RandomQuestionSetter delegate = new RandomQuestionSetter();

    /**
     * {@inheritDoc}
     * <p>Implementation: builds one shuffled pool per topic, then takes
     * one question per topic in topic order, cycling until {@code count}
     * is reached.</p>
     */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, int count) {
        List<List<Question>> pools = new ArrayList<>();
        for (Topic topic : Topic.values()) {
            List<Question> pool = new ArrayList<>(bank.getQuestionsByTopic(topic));
            if (!pool.isEmpty()) {
                java.util.Collections.shuffle(pool);
                pools.add(pool);
            }
        }

        List<Question> result = new ArrayList<>();
        int[] cursor = new int[pools.size()];
        while (result.size() < count) {
            boolean addedThisRound = false;
            for (int i = 0; i < pools.size() && result.size() < count; i++) {
                List<Question> pool = pools.get(i);
                if (cursor[i] < pool.size()) {
                    result.add(pool.get(cursor[i]++));
                    addedThisRound = true;
                }
            }
            if (!addedThisRound) {
                break; // all pools exhausted
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: a balanced selection constrained to one topic
     * simply degenerates to {@code count} questions from that topic.</p>
     */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, Topic topic, int count) {
        return delegate.selectQuestions(bank, topic, count);
    }

    /** {@inheritDoc} */
    @Override
    public String getStrategyName() {
        return "Balanced (all topics)";
    }
}
