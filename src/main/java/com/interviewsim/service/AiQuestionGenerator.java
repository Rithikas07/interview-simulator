package com.interviewsim.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.interviewsim.model.Question;
import com.interviewsim.model.Topic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates fresh interview questions using an AI model via an
 * {@link LlmClient}.
 * <p>
 * This makes the simulator's question source pluggable: the built-in
 * questions come from {@code questions.json}, but the bank can be extended
 * on demand with AI-generated questions on any chosen topic — great for
 * endless practice without manually writing content.
 * </p>
 *
 * <p>The generated questions follow the same schema as the JSON bank
 * ({@code topic}, {@code question}, {@code keywords}, {@code modelAnswer}),
 * so they are fully compatible with the evaluator and reports. Generated
 * questions are added to the session bank at runtime.</p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class AiQuestionGenerator {

    /** Instruction prompt for the question-writing persona. */
    private static final String SYSTEM_PROMPT = """
            You are an expert Java interviewer who writes placement interview
            questions for university students. Generate questions that are
            conceptual (not code-writing), at the level of campus placements.
            Respond ONLY with a JSON array; each element must be an object:
            {"topic": "<TOPIC name>", "question": "<question text>",
             "keywords": ["<keyword>", ...], "modelAnswer": "<concise model answer>"}
            Give 4-8 keywords per question. Output no prose, only the JSON array.""";

    /** Gson instance for parsing the generated JSON array. */
    private final Gson gson = new Gson();

    /** The LLM backend used to generate questions. */
    private final LlmClient llmClient;

    /** Supplies monotonically increasing ids for generated questions. */
    private int nextId;

    /**
     * Creates a generator bound to an LLM client, with ids starting after
     * the built-in bank's range.
     *
     * @param llmClient the AI backend to use
     * @param startId   first id to assign to generated questions
     */
    public AiQuestionGenerator(LlmClient llmClient, int startId) {
        this.llmClient = llmClient;
        this.nextId = startId;
    }

    /**
     * Generates a batch of questions, optionally focused on one topic.
     *
     * @param topic     the topic to focus on, or {@code null} for a mix
     * @param count     how many questions to request
     * @return the generated questions (may be empty on failure)
     * @throws AiEvaluationException on any AI failure
     */
    public List<Question> generate(Topic topic, int count) {
        String focus = topic == null
                ? "a balanced mix of all provided topics"
                : "the topic " + topic.getDisplayName() + " ONLY";
        String userPrompt = "Generate " + count + " Java interview questions on "
                + focus + ". Valid topic names: "
                + Topic.OOP + ", " + Topic.COLLECTIONS + ", " + Topic.STRINGS + ", "
                + Topic.EXCEPTIONS + ", " + Topic.JVM + ", " + Topic.MULTITHREADING
                + ".";

        String response = llmClient.chat(SYSTEM_PROMPT, userPrompt);
        List<Question> parsed = parseQuestions(response);
        parsed.forEach(q -> System.out.println("  + [AI] " + q.getTopic().getDisplayName()
                + ": " + truncate(q.getText(), 70)));
        return parsed;
    }

    /**
     * Parses the model's JSON array response into {@link Question} objects,
     * assigning fresh ids and validating topics.
     *
     * @param response the raw model text (possibly fenced with prose)
     * @return the parsed questions with valid topics only
     */
    private List<Question> parseQuestions(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start < 0 || end <= start) {
            throw new AiEvaluationException(
                    "No JSON array in AI question response");
        }
        String json = response.substring(start, end + 1);

        Type listType = new TypeToken<List<JsonObject>>() {}.getType();
        List<JsonObject> raw = gson.fromJson(json, listType);

        List<Question> result = new ArrayList<>();
        for (JsonObject item : raw) {
            try {
                Topic topic = Topic.valueOf(item.get("topic").getAsString()
                        .trim().toUpperCase().replace(' ', '_'));
                result.add(new Question(nextId++, topic,
                        item.get("question").getAsString(),
                        toStringList(item.getAsJsonArray("keywords")),
                        item.get("modelAnswer").getAsString()));
            } catch (Exception skip) {
                // Skip malformed entries rather than failing the whole batch
                System.err.println("  [WARN] skipping malformed generated question");
            }
        }
        return result;
    }

    /**
     * Converts a JSON array of strings to a {@code List<String>}.
     *
     * @param arr the JSON array
     * @return the string list
     */
    private List<String> toStringList(com.google.gson.JsonArray arr) {
        List<String> out = new ArrayList<>();
        arr.forEach(e -> out.add(e.getAsString()));
        return out;
    }

    /**
     * Truncates text for logging.
     *
     * @param text     the text
     * @param maxChars maximum length
     * @return the truncated string
     */
    private String truncate(String text, int maxChars) {
        return text.length() <= maxChars ? text : text.substring(0, maxChars) + "...";
    }
}
