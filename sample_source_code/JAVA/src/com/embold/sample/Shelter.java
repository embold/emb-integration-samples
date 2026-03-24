package com.embold.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Shelter holds a collection of animals.
 * Demonstrates composition: has-a relationship with IAnimal.
 * Multiple incoming calls from Report and Main.
 */
public class Shelter {

    private final String name;
    private final int capacity;
    private final List<IAnimal> animals;

    public Shelter(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.animals = new ArrayList<>();
    }

    public String getName() { return name; }

    /** Adds an animal; returns {@code false} if at capacity. */
    public boolean intake(IAnimal animal) {
        if (animals.size() >= capacity) return false;
        animals.add(animal);
        return true;
    }

    /** Removes and returns the named animal, or empty if not found. */
    public Optional<IAnimal> adopt(String animalName) {
        Optional<IAnimal> found = animals.stream()
                .filter(a -> a.getName().equalsIgnoreCase(animalName))
                .findFirst();
        found.ifPresent(animals::remove);
        return found;
    }

    public List<IAnimal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    public List<IAnimal> findBySpecies(String species) {
        return animals.stream()
                .filter(a -> a.getSpecies().equalsIgnoreCase(species))
                .collect(Collectors.toList());
    }

    public int availableCapacity() { return capacity - animals.size(); }

    public double averageAge() {
        return animals.stream().mapToInt(IAnimal::getAge).average().orElse(0.0);
    }
}
