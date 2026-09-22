package com.interviewsim.model;

/**
 * Enumeration of Java topics from which interview questions are drawn.
 * <p>
 * Covers the six core placement-preparation areas: OOP, Collections, Strings,
 * Exceptions, JVM and Multithreading.
 * </p>
 *
 * @author Interview Simulator Team
 * @version 1.0
 */
public enum Topic {

    /** Object-oriented programming: classes, inheritance, polymorphism, etc. */
    OOP("OOP"),

    /** The Java Collections Framework: List, Set, Map, Queue, etc. */
    COLLECTIONS("Collections"),

    /** String handling: immutability, StringBuilder, String pool, etc. */
    STRINGS("Strings"),

    /** Exception handling: try/catch, checked vs unchecked, finally, etc. */
    EXCEPTIONS("Exceptions"),

    /** JVM internals: memory areas, class loading, garbage collection. */
    JVM("JVM"),

    /** Multithreading and concurrency: threads, synchronization, executors. */
    MULTITHREADING("Multithreading");

    private final String displayName;

    /**
     * Constructs a topic with a human-readable display name.
     *
     * @param displayName the pretty name shown in reports and menus
     */
    Topic(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of this topic.
     *
     * @return display name such as "Multithreading"
     */
    public String getDisplayName() {
        return displayName;
    }
}
