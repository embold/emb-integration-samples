package com.embold.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatTest {

    private Cat indoorCat;
    private Cat outdoorCat;

    @BeforeEach
    void setUp() {
        indoorCat  = new Cat("Whiskers", 3, true,  4.5);  // ideal weight
        outdoorCat = new Cat("Shadow",   5, false, 7.0);  // above ideal weight
    }

    // ── basic properties ──────────────────────────────────────────────────────

    @Test
    void sound_returnsMeow() {
        assertEquals("Meow", indoorCat.sound());
    }

    @Test
    void isIndoor_reflectsConstructorFlag() {
        assertTrue(indoorCat.isIndoor());
        assertFalse(outdoorCat.isIndoor());
    }

    @Test
    void getters_returnConstructorValues() {
        assertEquals("Whiskers", indoorCat.getName());
        assertEquals(3, indoorCat.getAge());
        assertEquals("Felis catus", indoorCat.getSpecies());
        assertEquals(4.5, indoorCat.getWeight());
    }

    @Test
    void describe_containsNameAndSound() {
        String desc = indoorCat.describe();
        assertTrue(desc.contains("Whiskers"));
        assertTrue(desc.contains("Meow"));
    }

    // ── expectedLifespan ─────────────────────────────────────────────────────

    @Test
    void expectedLifespan_indoor_greaterThanOutdoor() {
        Cat indoor  = new Cat("A", 2, true,  4.5);
        Cat outdoor = new Cat("B", 2, false, 4.5);
        assertTrue(indoor.expectedLifespan() > outdoor.expectedLifespan());
    }

    @Test
    void expectedLifespan_atIdealWeight_noPenalty() {
        // base indoor=15, deviation=0 → no adjustment
        assertEquals(15, indoorCat.expectedLifespan());
    }

    @Test
    void expectedLifespan_isAtLeastFive() {
        // Very heavy outdoor cat — lifespan must not go below 5
        Cat heavyCat = new Cat("Fat", 10, false, 20.0);
        assertTrue(heavyCat.expectedLifespan() >= 5);
    }

    // ── visitor double-dispatch ───────────────────────────────────────────────

    @Test
    void accept_incrementsCatCount() {
        AnimalVisitor visitor = new AnimalVisitor();
        indoorCat.accept(visitor);
        assertEquals(1, visitor.getCatCount());
        assertEquals(0, visitor.getDogCount());
    }
}
