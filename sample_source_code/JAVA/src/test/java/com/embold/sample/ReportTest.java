package com.embold.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    private Shelter shelter;

    @BeforeEach
    void setUp() {
        shelter = new Shelter("Test Haven", 10);
        shelter.intake(new Dog("Rex",      3, "Labrador",  30.0));
        shelter.intake(new Dog("Buddy",    7, "Poodle",    15.0));
        shelter.intake(new Cat("Whiskers", 2, true,         4.5));
        shelter.intake(new Cat("Shadow",   5, false,        7.0));
    }

    @Test
    void generate_containsShelterName() {
        String report = new Report(shelter).generate();
        assertTrue(report.contains("Test Haven"));
    }

    @Test
    void generate_containsExpectedAnimalCounts() {
        String report = new Report(shelter).generate();
        assertTrue(report.contains("Dogs            : 2"));
        assertTrue(report.contains("Cats            : 2"));
        assertTrue(report.contains("Total animals   : 4"));
    }

    @Test
    void generate_containsAnimalDetails() {
        String report = new Report(shelter).generate();
        assertTrue(report.contains("Rex"));
        assertTrue(report.contains("Whiskers"));
    }

    @Test
    void generate_containsAvailableCapacity() {
        String report = new Report(shelter).generate();
        // 10 capacity - 4 animals = 6 available
        assertTrue(report.contains("Available cap   : 6"));
    }

    @Test
    void generate_emptyShelter_showsZeroCounts() {
        Shelter empty = new Shelter("Empty", 5);
        String report = new Report(empty).generate();
        assertTrue(report.contains("Total animals   : 0"));
        assertTrue(report.contains("Dogs            : 0"));
        assertTrue(report.contains("Cats            : 0"));
    }

    @Test
    void generate_returnsNonEmptyString() {
        String report = new Report(shelter).generate();
        assertNotNull(report);
        assertFalse(report.isBlank());
    }

    @Test
    void generate_calledTwice_returnsSameResult() {
        Report report = new Report(shelter);
        assertEquals(report.generate(), report.generate());
    }
}
