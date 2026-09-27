package com.interviewsim.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicTest {

    @Test
    void hasSixCoreTopics() {
        assertEquals(6, Topic.values().length);
    }

    @Test
    void displayNamesAreHumanReadable() {
        assertEquals("OOP", Topic.OOP.getDisplayName());
        assertEquals("Collections", Topic.COLLECTIONS.getDisplayName());
        assertEquals("Strings", Topic.STRINGS.getDisplayName());
        assertEquals("Exceptions", Topic.EXCEPTIONS.getDisplayName());
        assertEquals("JVM", Topic.JVM.getDisplayName());
        assertEquals("Multithreading", Topic.MULTITHREADING.getDisplayName());
    }

    @Test
    void valueOfResolvesEnumConstantFromName() {
        assertEquals(Topic.COLLECTIONS, Topic.valueOf("COLLECTIONS"));
    }
}
