package com.interviewsim;

import com.interviewsim.core.InterviewSession;
import com.interviewsim.model.AnswerEvaluation;
import com.interviewsim.model.Candidate;
import com.interviewsim.model.Topic;
import com.interviewsim.service.InMemoryQuestionBank;
import com.interviewsim.service.KeywordBasedEvaluator;
import com.interviewsim.service.PlacementReadinessReportGenerator;
import com.interviewsim.service.QuestionBank;
import com.interviewsim.service.ReportGenerator;
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
    private static final int MENU_EXIT = 4;

    private final Scanner scanner = new Scanner(System.in);
    private final QuestionBank questionBank = new InMemoryQuestionBank();
    private final KeywordBasedEvaluator evaluator = new KeywordBasedEvaluator();
    private final Candidate candidate;

    /**
     * Constructs the application; immediately registers the candidate.
     */
    public InterviewSimulatorApp() {
        this.candidate = new CandidateRegistrationUI(scanner).register();
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
                case MENU_EXIT -> {
                    System.out.println("\nThank you for practising with the Java AI Interview Simulator. All the best!");
                    running = false;
                }
                default -> System.out.println("Invalid option, please choose 1-4.\n");
            }
        }
        scanner.close();
    }

    /**
     * Runs one full interview session and prints the readiness report.
     *
     * @return {@code true} to keep the menu loop running
     */
    private boolean startInterview() {
        ConsoleInterviewUI ui = new ConsoleInterviewUI(scanner);
        InterviewSession session = new InterviewSession(questionBank, evaluator, candidate);
        List<AnswerEvaluation> evaluations = session.run(ui);

        ReportGenerator reportGenerator = new PlacementReadinessReportGenerator(candidate);
        System.out.println();
        System.out.println(reportGenerator.generateReport(candidate, evaluations));
        return true;
    }

    /**
     * Lets the user browse the question bank by topic.
     */
    private void browseQuestions() {
        System.out.println("\nAvailable topics:");
        Topic[] topics = Topic.values();
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
        System.out.println(" 4. Exit");
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
