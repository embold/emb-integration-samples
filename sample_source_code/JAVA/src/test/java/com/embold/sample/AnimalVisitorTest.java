package com.embold.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnimalVisitorTest {

    private AnimalVisitor visitor;
    private Dog  labrador;
    private Cat  indoorCat;

    @BeforeEach
    void setUp() {
        visitor   = new AnimalVisitor();
        labrador  = new Dog("Rex",      3, "Labrador", 30.0);
        indoorCat = new Cat("Whiskers", 2, true,        4.5);
    }

    // ── initial state ─────────────────────────────────────────────────────────

    @Test
    void initialCounts_areZero() {
        assertEquals(0, visitor.getDogCount());
        assertEquals(0, visitor.getCatCount());
        assertEquals(0, visitor.getTotalCount());
    }

    @Test
    void initialAverageHealthScore_isZero() {
        assertEquals(0.0, visitor.getAverageHealthScore());
    }

    // ── visit Dog ─────────────────────────────────────────────────────────────

    @Test
    void visitDog_incrementsDogCount() {
        visitor.visit(labrador);
        assertEquals(1, visitor.getDogCount());
        assertEquals(0, visitor.getCatCount());
    }

    @Test
    void visitDog_updatesTotalCount() {
        visitor.visit(labrador);
        assertEquals(1, visitor.getTotalCount());
    }

    @Test
    void visitDog_updatesSpeciesCount() {
        visitor.visit(labrador);
        Map<String, Integer> counts = visitor.getSpeciesCount();
        assertEquals(1, counts.get("Dog"));
    }

    @Test
    void visitDog_averageHealthScoreIsPositive() {
        visitor.visit(labrador);
        assertTrue(visitor.getAverageHealthScore() > 0.0);
    }

    // ── visit Cat ─────────────────────────────────────────────────────────────

    @Test
    void visitCat_incrementsCatCount() {
        visitor.visit(indoorCat);
        assertEquals(1, visitor.getCatCount());
        assertEquals(0, visitor.getDogCount());
    }

    @Test
    void visitCat_atIdealWeight_healthScoreNearHundred() {
        visitor.visit(indoorCat);   // weight=4.5 == ideal
        assertEquals(100.0, visitor.getAverageHealthScore(), 1e-6);
    }

    // ── mixed visits ─────────────────────────────────────────────────────────

    @Test
    void mixedVisits_countsAreCorrect() {
        visitor.visit(labrador);
        visitor.visit(indoorCat);
        visitor.visit(new Dog("Max", 5, "Poodle", 15.0));

        assertEquals(2, visitor.getDogCount());
        assertEquals(1, visitor.getCatCount());
        assertEquals(3, visitor.getTotalCount());
    }

    @Test
    void averageHealthScore_afterMultiple_isWithinRange() {
        visitor.visit(labrador);
        visitor.visit(indoorCat);
        double avg = visitor.getAverageHealthScore();
        assertTrue(avg >= 0.0 && avg <= 100.0);
    }

    // ── reset ─────────────────────────────────────────────────────────────────

    @Test
    void reset_clearsAllCounters() {
        visitor.visit(labrador);
        visitor.visit(indoorCat);
        visitor.reset();

        assertEquals(0, visitor.getDogCount());
        assertEquals(0, visitor.getCatCount());
        assertEquals(0, visitor.getTotalCount());
        assertEquals(0.0, visitor.getAverageHealthScore());
        assertTrue(visitor.getSpeciesCount().isEmpty());
    }
}
