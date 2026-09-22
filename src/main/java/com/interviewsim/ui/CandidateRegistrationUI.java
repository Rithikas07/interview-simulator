package com.interviewsim.ui;

import com.interviewsim.model.Candidate;

import java.util.Scanner;

/**
 * Handles candidate registration (profile creation) via the console.
 * <p>
 * Prompts for name, university and roll number and returns a fully
 * constructed {@link Candidate} profile.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public class CandidateRegistrationUI {

    private final Scanner scanner;

    /**
     * Constructs the registration UI.
     *
     * @param scanner the scanner used for user input
     */
    public CandidateRegistrationUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Runs the registration flow and builds the candidate profile.
     *
     * @return a new {@link Candidate} with the entered details
     */
    public Candidate register() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("           CANDIDATE PROFILE REGISTRATION");
        System.out.println("================================================================");
        String name = prompt("Enter your full name    : ");
        String university = prompt("Enter your university   : ");
        String rollNumber = prompt("Enter your roll number  : ");
        System.out.println("Profile created. Good luck, " + name + "!");
        return new Candidate(name, university, rollNumber);
    }

    /**
     * Prints a prompt and reads one trimmed line of input.
     * Falls back to "Unknown" if the user enters blank text.
     *
     * @param message the prompt to display
     * @return the trimmed input value
     */
    private String prompt(String message) {
        System.out.print(message);
        String value = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        return value.isEmpty() ? "Unknown" : value;
    }
}
