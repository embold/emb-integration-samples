package com.embold.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DogTest {

    private Dog labrador;
    private Dog chihuahua;
    private Dog defaultBreed;

    @BeforeEach
    void setUp() {
        labrador   = new Dog("Rex",    3, "Labrador",  30.0);
        chihuahua  = new Dog("Tiny",   1, "Chihuahua",  2.5);
        defaultBreed = new Dog("Buddy", 8, "Poodle",   15.0);
    }

    // ── basic properties ──────────────────────────────────────────────────────

    @Test
    void getBreed_returnsCorrectBreed() {
        assertEquals("Labrador", labrador.getBreed());
    }

    @Test
    void sound_returnsWoof() {
        assertEquals("Woof", labrador.sound());
    }

    @Test
    void getters_returnConstructorValues() {
        assertEquals("Rex", labrador.getName());
        assertEquals(3, labrador.getAge());
        assertEquals("Canis lupus familiaris", labrador.getSpecies());
        assertEquals(30.0, labrador.getWeight());
    }

    @Test
    void describe_containsNameAndSound() {
        String desc = labrador.describe();
        assertTrue(desc.contains("Rex"));
        assertTrue(desc.contains("Woof"));
    }

    // ── computeHealthScore ────────────────────────────────────────────────────

    @Test
    void healthScore_idealWeightAndYoung_nearHundred() {
        // chihuahua at ideal weight (2.5 kg) age 1 → score should be high
        double score = chihuahua.computeHealthScore();
        assertTrue(score >= 90.0, "Expected score >= 90, got " + score);
    }

    @Test
    void healthScore_isWithinZeroToHundred() {
        assertTrue(labrador.computeHealthScore() >= 0.0);
        assertTrue(labrador.computeHealthScore() <= 100.0);
        assertTrue(defaultBreed.computeHealthScore() >= 0.0);
        assertTrue(defaultBreed.computeHealthScore() <= 100.0);
    }

    @Test
    void healthScore_olderDog_lowerThanYounger() {
        Dog young = new Dog("A", 2, "Labrador", 30.0);
        Dog old   = new Dog("B", 12, "Labrador", 30.0);
        assertTrue(old.computeHealthScore() < young.computeHealthScore());
    }

    // ── visitor double-dispatch ───────────────────────────────────────────────

    @Test
    void accept_incrementsDogCount() {
        AnimalVisitor visitor = new AnimalVisitor();
        labrador.accept(visitor);
        assertEquals(1, visitor.getDogCount());
        assertEquals(0, visitor.getCatCount());
    }
}
