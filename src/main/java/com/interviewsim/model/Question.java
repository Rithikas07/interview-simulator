package com.interviewsim.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents a single Java interview question.
 * <p>
 * Each question belongs to a {@link Topic}, stores the question text, the list
 * of expected keywords used for rule-based AI evaluation, and the model
 * (ideal) answer shown after the candidate responds.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class Question {

    @SerializedName("id")
    private final int id;

    @SerializedName("topic")
    private final Topic topic;

    /** Maps to the {@code "question"} key in questions.json. */
    @SerializedName("question")
    private final String text;

    /** Maps to the {@code "keywords"} key in questions.json. */
    @SerializedName("keywords")
    private final List<String> expectedKeywords;

    @SerializedName("modelAnswer")
    private final String modelAnswer;

    /**
     * Public constructor used when creating questions programmatically.
     *
     * @param id                unique question identifier
     * @param topic             the topic category the question belongs to
     * @param text              the question text shown to the candidate
     * @param expectedKeywords  keywords the AI evaluator looks for in the answer
     * @param modelAnswer       the ideal reference answer displayed as feedback
     */
    public Question(int id, Topic topic, String text,
                    List<String> expectedKeywords, String modelAnswer) {
        this.id = id;
        this.topic = topic;
        this.text = text;
        this.expectedKeywords = expectedKeywords;
        this.modelAnswer = modelAnswer;
    }

    /**
     * No-arg constructor required by Gson for reflective deserialization.
     */
    @SuppressWarnings("unused")
    private Question() {
        this(-1, null, "", List.of(), "");
    }

    /**
     * Returns the unique identifier of this question.
     *
     * @return the question id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the topic category of this question.
     *
     * @return the {@link Topic} of the question
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Returns the question text.
     *
     * @return the question prompt
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the expected keywords for AI evaluation.
     *
     * @return an unmodifiable view of the expected keywords
     */
    public List<String> getExpectedKeywords() {
        return java.util.Collections.unmodifiableList(expectedKeywords);
    }

    /**
     * Returns the model (ideal) answer for this question.
     *
     * @return the model answer text
     */
    public String getModelAnswer() {
        return modelAnswer;
    }
}
