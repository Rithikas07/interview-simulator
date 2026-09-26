package com.interviewsim.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared skeleton for LLM-backed {@link AiAnswerEvaluator}s.
 * <p>
 * Implements the Template Method + Strategy patterns: this base class owns
 * the interviewer prompt, JSON verdict parsing and keyword statistics,
 * while the actual model communication is delegated entirely to an
 * injected {@link LlmClient} implementation (Claude, Gemini, Ollama...).
 * One evaluator interface, multiple provider clients.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.1
 */
public abstract class AbstractLlmEvaluator implements AiAnswerEvaluator {

    /** Shared Gson instance for parsing model responses. */
    protected final Gson gson = new Gson();

    /** The AI backend used for evaluation. */
    private final LlmClient llmClient;

    /** Interviewer persona and output-format instructions sent as system prompt. */
    private static final String SYSTEM_PROMPT = """
            You are a strict but fair Java technical interviewer evaluating a
            university student's placement interview answer. Evaluate the answer
            against the question, the expected keywords and the model answer.
            Respond ONLY with a JSON object of the form:
            {"score": <integer 0-100>, "feedback": "<one short paragraph>"}
            Score rubric: 90-100 comprehensive and correct; 70-89 mostly correct
            with minor gaps; 40-69 partially correct, key concept missing;
            1-39 largely incorrect; 0 empty/no attempt. Feedback must mention
            what was good, what was missing, and/or any factual errors.""";

    /**
     * Creates an evaluator bound to an LLM client.
     *
     * @param llmClient the AI backend performing the evaluation
     */
    protected AbstractLlmEvaluator(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable() {
        return llmClient.isAvailable();
    }

    /**
     * Returns the system prompt describing the interviewer persona and the
     * required JSON output format.
     *
     * @return the system prompt text
     */
    protected String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Evaluates the answer via the LLM client and parses the verdict.
     *
     * @param question        the question being answered
     * @param candidateAnswer the candidate's raw answer text
     * @return the AI evaluation result
     */
    @Override
    public AnswerEvaluation evaluate(Question question, String candidateAnswer) {
        if (!isAvailable()) {
            throw new AiEvaluationException(
                    llmClient.getProviderName() + " is not configured");
        }
        String response = llmClient.chat(getSystemPrompt(),
                buildUserPrompt(question, candidateAnswer));
        return parseEvaluation(question, candidateAnswer, response);
    }

    /**
     * Parses the model's JSON verdict into an {@link AnswerEvaluation}.
     * Keyword hit/miss lists are still computed locally for explainability.
     *
     * @param question        the original question
     * @param candidateAnswer the candidate's raw answer
     * @param aiResponse      the model's text containing the JSON verdict
     * @return the evaluation result
     */
    protected AnswerEvaluation parseEvaluation(Question question, String candidateAnswer,
                                                String aiResponse) {
        String jsonText = extractJson(aiResponse);
        int score;
        String feedback;
        try {
            JsonObject verdict = gson.fromJson(jsonText, JsonObject.class);
            int raw = verdict.get("score").getAsInt();
            score = Math.max(0, Math.min(100, raw));
            feedback = verdict.get("feedback").getAsString();
        } catch (Exception e) {
            throw new AiEvaluationException(
                    "Could not parse AI verdict", e);
        }
        List<String> matched = new ArrayList<>();
        List<String> missed = new ArrayList<>();
        String normalized = candidateAnswer == null ? "" : candidateAnswer.toLowerCase();
        for (String k : question.getExpectedKeywords()) {
            (normalized.contains(k.toLowerCase()) ? matched : missed).add(k);
        }
        return new AnswerEvaluation(question, candidateAnswer, score, matched, missed,
                "[AI EVALUATOR] " + feedback);
    }

    /**
     * Extracts the first JSON object from a response, tolerating markdown
     * fences or surrounding prose.
     *
     * @param text the raw model response
     * @return the JSON substring
     */
    protected String extractJson(String text) {
        if (text == null) {
            throw new AiEvaluationException("Null AI response");
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new AiEvaluationException(
                    "No JSON object in AI response");
        }
        return text.substring(start, end + 1);
    }

    /**
     * Builds the user prompt for the given question and candidate answer.
     *
     * @param question        the question being asked
     * @param candidateAnswer the candidate's raw answer
     * @return the prompt text
     */
    private String buildUserPrompt(Question question, String candidateAnswer) {
        return "QUESTION: " + question.getText()
                + "\nEXPECTED KEYWORDS: " + String.join(", ", question.getExpectedKeywords())
                + "\nMODEL ANSWER: " + question.getModelAnswer()
                + "\n\nCANDIDATE ANSWER: "
                + (candidateAnswer == null || candidateAnswer.isBlank()
                        ? "(no answer given)" : candidateAnswer);
    }
}
