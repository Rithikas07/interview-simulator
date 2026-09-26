package com.interviewsim;

import com.interviewsim.core.InterviewSession;
import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Topic;
import com.interviewsim.service.InMemoryQuestionBank;
import com.interviewsim.service.AiAnswerEvaluator;
import com.interviewsim.service.AiQuestionGenerator;
import com.interviewsim.service.AnswerEvaluator;
import com.interviewsim.service.BalancedQuestionSetter;
import com.interviewsim.service.ClaudeAnswerEvaluator;
import com.interviewsim.service.EvaluatorFactory;
import com.interviewsim.service.FallbackEvaluator;
import com.interviewsim.service.GeminiAnswerEvaluator;
import com.interviewsim.service.KeywordBasedEvaluator;
import com.interviewsim.service.LlmClient;
import com.interviewsim.service.OllamaAnswerEvaluator;
import com.interviewsim.service.OllamaLlmClient;
import com.interviewsim.service.PlacementReadinessReportGenerator;
import com.interviewsim.service.QuestionBank;
import com.interviewsim.service.QuestionSetter;
import com.interviewsim.service.RandomQuestionSetter;
import com.interviewsim.service.ReportGenerator;
import com.interviewsim.service.TopicQuestionSetter;
import com.interviewsim.ui.CandidateRegistrationUI;
import com.interviewsim.ui.ConsoleInterviewUI;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the Java AI Interview Simulator.
 * <p>
 * A console-based placement-preparation application for Java students.
 * The main loop registers a candidate, then repeatedly offers a menu:
 * start an interview, browse questions by topic, view the profile, or exit.
 * After each interview a full placement-readiness report is printed,
 * including per-topic breakdown, best score and completed interview count.
 * </p>
 *
 * <p>Architecture (clear separation of concerns):</p>
 * <ul>
 *   <li>{@code model}     — data classes (Candidate, Question, Topic, AnswerEvaluation)</li>
 *   <li>{@code service}   — interfaces + implementations (QuestionBank, AnswerEvaluator, ReportGenerator)</li>
 *   <li>{@code core}      — the interview engine (InterviewSession + InterviewListener)</li>
 *   <li>{@code ui}        — console front-end classes</li>
 * </ul>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class InterviewSimulatorApp {

    private static final int MENU_START = 1;
    private static final int MENU_BROWSE = 2;
    private static final int MENU_PROFILE = 3;
    private static final int MENU_GENERATE = 4;
    private static final int MENU_EXIT = 5;

    private final Scanner scanner = new Scanner(System.in);
    private final QuestionBank questionBank = new InMemoryQuestionBank();
    private final AnswerEvaluator evaluator;
    private final Candidate candidate;

    /**
     * Constructs the application; immediately registers the candidate and
     * selects the best available AI evaluator via the factory.
     */
    public InterviewSimulatorApp() {
        this.candidate = new CandidateRegistrationUI(scanner).register();
        this.evaluator = EvaluatorFactory.selectBestEvaluator();
        System.out.println();
        System.out.println("[EVALUATOR] " + EvaluatorFactory.describeSelectedEvaluator());
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new InterviewSimulatorApp().run();
    }

    /**
     * Runs the main menu loop until the user chooses to exit.
     */
    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case MENU_START -> running = startInterview();
                case MENU_BROWSE -> browseQuestions();
                case MENU_PROFILE -> showProfile();
                case MENU_GENERATE -> generateAiQuestions();
                case MENU_EXIT -> {
                    System.out.println("\nThank you for practising with the Java AI Interview Simulator. All the best!");
                    running = false;
                }
                default -> System.out.println("Invalid option, please choose 1-5.\n");
            }
        }
        scanner.close();
    }

    /**
     * Runs one full interview session and prints the readiness report.
     * <p>Asks the user which question-setting strategy to use first.</p>
     *
     * @return {@code true} to keep the menu loop running
     */
    private boolean startInterview() {
        QuestionSetter setter = chooseQuestionSetter();
        if (setter == null) {
            return true; // cancelled
        }
        ConsoleInterviewUI ui = new ConsoleInterviewUI(scanner);
        InterviewSession session = new InterviewSession(
                questionBank, evaluator, candidate, setter);
        List<AnswerEvaluation> evaluations = session.run(ui);

        ReportGenerator reportGenerator = new PlacementReadinessReportGenerator(candidate);
        System.out.println();
        System.out.println(reportGenerator.generateReport(candidate, evaluations));
        return true;
    }

    /**
     * Prompts the user to pick a question-setting strategy
     * (random / balanced / topic focus).
     *
     * @return the chosen strategy, or {@code null} if cancelled
     */
    private QuestionSetter chooseQuestionSetter() {
        System.out.println("\nQuestion setting strategy:");
        System.out.println("  1. Random        (shuffled from entire bank)");
        System.out.println("  2. Balanced      (even coverage of all topics)");
        System.out.println("  3. Topic focus   (one topic only)");
        int choice = readInt("Choose (0 to cancel): ");
        return switch (choice) {
            case 1 -> new RandomQuestionSetter();
            case 2 -> new BalancedQuestionSetter();
            case 3 -> {
                Topic topic = chooseTopic();
                yield topic == null ? null : new TopicQuestionSetter(topic);
            }
            default -> null;
        };
    }

    /**
     * Prompts the user to pick one topic.
     *
     * @return the chosen topic, or {@code null} if cancelled
     */
    private Topic chooseTopic() {
        Topic[] topics = Topic.values();
        System.out.println("\nTopics:");
        for (int i = 0; i < topics.length; i++) {
            System.out.printf("  %d. %s (%d questions)%n", i + 1,
                    topics[i].getDisplayName(),
                    questionBank.getQuestionsByTopic(topics[i]).size());
        }
        int pick = readInt("Choose topic (0 to cancel): ");
        return (pick > 0 && pick <= topics.length) ? topics[pick - 1] : null;
    }

    /**
     * Generates new questions with AI and adds them to the bank.
     */
    private void generateAiQuestions() {
        AiAnswerEvaluator ai = null;
        for (AiAnswerEvaluator candidate : EvaluatorFactory.getAiEvaluators()) {
            if (candidate.isAvailable()) {
                ai = candidate;
                break;
            }
        }
        if (ai == null) {
            System.out.println("\nAI generation unavailable — no AI backend configured.");
            System.out.println("Set ANTHROPIC_API_KEY / GEMINI_API_KEY, or run Ollama locally.");
            return;
        }
        // Reuse the first available LlmClient via the matching provider class
        LlmClient client = findClientFor(ai);
        Topic topic = chooseTopic();
        System.out.println("\nGenerating 3 AI questions "
                + (topic == null ? "(mixed topics)" : "on " + topic.getDisplayName())
                + "... this may take a moment.");
        try {
            List<com.interviewsim.model.Question> generated =
                    new AiQuestionGenerator(client, 1000).generate(topic, 3);
            questionBank.addQuestions(generated);
            System.out.println("Added " + generated.size()
                    + " AI-generated question(s) to the bank. Bank size now: "
                    + questionBank.getAllQuestions().size());
        } catch (RuntimeException e) {
            System.out.println("AI generation failed: " + e.getMessage());
        }
    }

    /**
     * Finds the {@link LlmClient} matching the chosen evaluator's provider.
     *
     * @param evaluator the selected AI evaluator
     * @return a corresponding LLM client
     */
    private LlmClient findClientFor(AiAnswerEvaluator evaluator) {
        if (evaluator instanceof ClaudeAnswerEvaluator) {
            return new com.interviewsim.service.ClaudeLlmClient();
        }
        if (evaluator instanceof GeminiAnswerEvaluator) {
            return new com.interviewsim.service.GeminiLlmClient();
        }
        if (evaluator instanceof OllamaAnswerEvaluator) {
            return new com.interviewsim.service.OllamaLlmClient();
        }
        throw new IllegalStateException("Unknown evaluator provider");
    }

    /**
     * Lets the user browse the question bank by topic.
     */
    private void browseQuestions() {
        Topic[] topics = Topic.values();
        System.out.println("\nAvailable topics:");
        for (int i = 0; i < topics.length; i++) {
            System.out.printf("  %d. %s (%d questions)%n",
                    i + 1, topics[i].getDisplayName(),
                    questionBank.getQuestionsByTopic(topics[i]).size());
        }
        int pick = readInt("Choose a topic number (0 to cancel): ");
        if (pick <= 0 || pick > topics.length) {
            return;
        }
        List<com.interviewsim.model.Question> questions =
                questionBank.getQuestionsByTopic(topics[pick - 1]);
        System.out.println("\n--- " + topics[pick - 1].getDisplayName() + " questions ---");
        for (var q : questions) {
            System.out.println("- " + q.getText());
        }
        System.out.println();
    }

    /**
     * Displays the candidate profile and lifetime statistics.
     */
    private void showProfile() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("                      CANDIDATE PROFILE");
        System.out.println("================================================================");
        System.out.println(candidate);
        System.out.println("================================================================\n");
    }

    /**
     * Prints the main menu.
     */
    private void printMenu() {
        System.out.println("\n******************* MAIN MENU *******************");
        System.out.println(" 1. Start mock interview");
        System.out.println(" 2. Browse questions by topic");
        System.out.println(" 3. View my profile & statistics");
        System.out.println(" 4. Generate AI questions (adds to bank)");
        System.out.println(" 5. Exit");
        System.out.println("*************************************************");
    }

    /**
     * Reads an integer from the console, re-prompting on bad input.
     *
     * @param prompt the message to display
     * @return the parsed integer, or -1 if input is invalid
     */
    private int readInt(String prompt) {
        System.out.print(prompt);
        String line = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
