package com.interviewsim.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads the question bank from an external JSON file on the classpath
 * using the Gson library.
 * <p>
 * The JSON schema for each question is:
 * <pre>{@code
 * {
 *   "id": 1,
 *   "topic": "OOP",
 *   "question": "...",
 *   "keywords": ["..."],
 *   "modelAnswer": "..."
 * }
 * }</pre>
 * </p>
 * <p>
 * The {@code topic} string is mapped to the {@link com.interviewsim.model.Topic}
 * enum via a custom Gson deserializer, keeping domain validation strict.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public final class JsonQuestionLoader {

    /** Default classpath location of the question bank file. */
    public static final String DEFAULT_QUESTION_FILE = "/questions.json";

    private final Gson gson;

    /**
     * Creates a loader with a preconfigured Gson instance, including the
     * topic-enum deserializer.
     */
    public JsonQuestionLoader() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(com.interviewsim.model.Topic.class,
                        (JsonDeserializer<com.interviewsim.model.Topic>)
                                (json, type, ctx) ->
                                        com.interviewsim.model.Topic
                                                .valueOf(json.getAsString().toUpperCase()))
                .create();
    }

    /**
     * Loads all questions from the default classpath file ({@code /questions.json}).
     *
     * @return the parsed list of questions (never null; empty on unreadable file)
     */
    public List<com.interviewsim.model.Question> loadDefault() {
        return load(DEFAULT_QUESTION_FILE);
    }

    /**
     * Loads all questions from a given classpath resource.
     * <p>
     * Any I/O or parsing problem results in an empty list and a console
     * warning, so the application can still start with zero questions
     * rather than crashing.
     * </p>
     *
     * @param classpathResource the classpath path, e.g. {@code /questions.json}
     * @return the parsed list of questions, or an empty list on failure
     */
    public List<com.interviewsim.model.Question> load(String classpathResource) {
        try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IOException("Question bank not found on classpath: "
                        + classpathResource);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                com.google.gson.reflect.TypeToken<List<com.interviewsim.model.Question>> type =
                        new com.google.gson.reflect.TypeToken<>() {};
                List<com.interviewsim.model.Question> result = gson.fromJson(reader, type.getType());
                return result == null ? List.of() : result;
            }
        } catch (Exception e) {
            System.err.println("[WARN] Could not load question bank from "
                    + classpathResource + ": " + e.getMessage());
            return List.of();
        }
    }
}
