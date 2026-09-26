package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;

import java.util.Collections;
import java.util.List;

/**
 * Question setter that focuses an entire session on one chosen topic —
 * ideal for targeted revision ("drill me on Multithreading only").
 * <p>
 * Delegates the actual picking-within-topic to a
 * {@link RandomQuestionSetter} so topic order stays random.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class TopicQuestionSetter implements QuestionSetter {

    /** Default topic used when the caller has not chosen one. */
    private static final Topic DEFAULT_TOPIC = Topic.OOP;

    private final RandomQuestionSetter delegate = new RandomQuestionSetter();
    private final Topic topic;

    /**
     * Creates a topic setter with a default topic.
     */
    public TopicQuestionSetter() {
        this(DEFAULT_TOPIC);
    }

    /**
     * Creates a topic setter bound to the given topic.
     *
     * @param topic the topic all selected questions must belong to
     */
    public TopicQuestionSetter(Topic topic) {
        this.topic = topic;
    }

    /**
     * Returns the topic this setter is bound to.
     *
     * @return the bound topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * {@inheritDoc}
     * <p>Implementation: ignores the general case and always returns
     * questions from the bound topic (up to {@code count}).</p>
     */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, int count) {
        return selectQuestions(bank, topic, count);
    }

    /** {@inheritDoc} */
    @Override
    public List<Question> selectQuestions(QuestionBank bank, Topic topic, int count) {
        return delegate.selectQuestions(bank, topic, count);
    }

    /** {@inheritDoc} */
    @Override
    public String getStrategyName() {
        return "Topic focus: " + topic.getDisplayName();
    }
}
