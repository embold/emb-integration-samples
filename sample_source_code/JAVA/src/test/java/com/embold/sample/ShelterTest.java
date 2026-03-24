package com.embold.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShelterTest {

    private Shelter shelter;
    private Dog rex;
    private Cat whiskers;

    @BeforeEach
    void setUp() {
        shelter   = new Shelter("Happy Paws", 5);
        rex       = new Dog("Rex",      3, "Labrador", 30.0);
        whiskers  = new Cat("Whiskers", 2, true,        4.5);
    }

    // ── getName ───────────────────────────────────────────────────────────────

    @Test
    void getName_returnsConstructorValue() {
        assertEquals("Happy Paws", shelter.getName());
    }

    // ── intake ────────────────────────────────────────────────────────────────

    @Test
    void intake_underCapacity_returnsTrue() {
        assertTrue(shelter.intake(rex));
    }

    @Test
    void intake_addsAnimalToList() {
        shelter.intake(rex);
        assertEquals(1, shelter.getAnimals().size());
    }

    @Test
    void intake_atCapacity_returnsFalse() {
        Shelter tiny = new Shelter("Tiny", 1);
        tiny.intake(rex);
        assertFalse(tiny.intake(whiskers));
    }

    @Test
    void intake_atCapacity_doesNotAddAnimal() {
        Shelter tiny = new Shelter("Tiny", 1);
        tiny.intake(rex);
        tiny.intake(whiskers);
        assertEquals(1, tiny.getAnimals().size());
    }

    // ── adopt ─────────────────────────────────────────────────────────────────

    @Test
    void adopt_existingAnimal_returnsPresent() {
        shelter.intake(rex);
        Optional<IAnimal> adopted = shelter.adopt("Rex");
        assertTrue(adopted.isPresent());
        assertEquals("Rex", adopted.get().getName());
    }

    @Test
    void adopt_existingAnimal_removesFromShelter() {
        shelter.intake(rex);
        shelter.adopt("Rex");
        assertTrue(shelter.getAnimals().isEmpty());
    }

    @Test
    void adopt_caseInsensitive_works() {
        shelter.intake(rex);
        assertTrue(shelter.adopt("rex").isPresent());
    }

    @Test
    void adopt_unknownName_returnsEmpty() {
        shelter.intake(rex);
        assertTrue(shelter.adopt("Unknown").isEmpty());
    }

    // ── findBySpecies ─────────────────────────────────────────────────────────

    @Test
    void findBySpecies_returnsMatchingAnimals() {
        shelter.intake(rex);
        shelter.intake(whiskers);
        List<IAnimal> cats = shelter.findBySpecies("Felis catus");
        assertEquals(1, cats.size());
        assertEquals("Whiskers", cats.get(0).getName());
    }

    @Test
    void findBySpecies_caseInsensitive_works() {
        shelter.intake(rex);
        List<IAnimal> dogs = shelter.findBySpecies("CANIS LUPUS FAMILIARIS");
        assertEquals(1, dogs.size());
    }

    @Test
    void findBySpecies_noMatch_returnsEmptyList() {
        shelter.intake(rex);
        assertTrue(shelter.findBySpecies("Felis catus").isEmpty());
    }

    // ── availableCapacity ─────────────────────────────────────────────────────

    @Test
    void availableCapacity_emptySheter_equalsCapacity() {
        assertEquals(5, shelter.availableCapacity());
    }

    @Test
    void availableCapacity_afterIntake_decreases() {
        shelter.intake(rex);
        assertEquals(4, shelter.availableCapacity());
    }

    // ── averageAge ────────────────────────────────────────────────────────────

    @Test
    void averageAge_emptyShelter_returnsZero() {
        assertEquals(0.0, shelter.averageAge());
    }

    @Test
    void averageAge_multipleAnimals_isCorrect() {
        shelter.intake(rex);       // age 3
        shelter.intake(whiskers);  // age 2
        assertEquals(2.5, shelter.averageAge(), 1e-9);
    }

    // ── getAnimals (unmodifiable view) ────────────────────────────────────────

    @Test
    void getAnimals_returnsUnmodifiableList() {
        shelter.intake(rex);
        List<IAnimal> view = shelter.getAnimals();
        assertThrows(UnsupportedOperationException.class, () -> view.add(whiskers));
    }
}
