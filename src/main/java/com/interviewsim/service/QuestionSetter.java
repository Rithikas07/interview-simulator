package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;

import java.util.List;

/**
 * Contract for question <em>setting</em> strategies — i.e. how the set of
 * questions for an interview session is chosen and ordered.
 * <p>
 * This is deliberately separate from {@link QuestionBank} (which only
 * stores and supplies questions). A setter decides the <em>selection
 * policy</em>: fully random, topic-focused, balanced across topics, and so
 * on. One interface, multiple interchangeable implementations — the classic
 * Strategy pattern taught in OOP courses.
 * </p>
 *
 * <p>Known implementations:</p>
 * <ul>
 *   <li>{@link RandomQuestionSetter} — shuffled random selection</li>
 *   <li>{@link TopicQuestionSetter} — questions from one chosen topic</li>
 *   <li>{@link BalancedQuestionSetter} — even coverage of all topics</li>
 * </ul>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface QuestionSetter {

    /**
     * Selects the question set for one interview session.
     *
     * @param bank  the question bank to select from
     * @param count the desired number of questions
     * @return the selected questions, in presentation order
     */
    List<Question> selectQuestions(QuestionBank bank, int count);

    /**
     * Selects question set constrained to a single topic.
     *
     * @param bank  the question bank to select from
     * @param topic the topic to focus on
     * @param count the desired number of questions
     * @return the selected questions for the given topic
     */
    List<Question> selectQuestions(QuestionBank bank, Topic topic, int count);

    /**
     * Returns a short human-readable name of this selection strategy,
     * used in menus and reports (e.g. {@code "Random"}, {@code "Balanced"}).
     *
     * @return the strategy display name
     */
    String getStrategyName();
}
