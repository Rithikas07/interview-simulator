package com.interviewsim.service;

import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;
import java.util.List;

/**
 * Contract for a question bank / repository.
 * <p>
 * Implementations are responsible for storing questions and retrieving a
 * randomized selection for an interview session. This abstraction keeps the
 * rest of the application independent of how questions are stored (in-memory
 * list, file, database, etc.).
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public interface QuestionBank {

    /**
     * Returns all questions available in the bank.
     *
     * @return a list of all questions
     */
    List<Question> getAllQuestions();

    /**
     * Returns a random selection of questions for one interview session.
     *
     * @param count the number of random questions to return
     * @return a randomly ordered list of questions
     */
    List<Question> getRandomQuestions(int count);

    /**
     * Returns all questions belonging to a given topic.
     *
     * @param topic the topic to filter by
     * @return a list of questions in that topic
     */
    List<Question> getQuestionsByTopic(Topic topic);
}
