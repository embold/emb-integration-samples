package com.embold.sample;

/**
 * Abstract base class implementing IAnimal.
 * Provides shared state and a default implementation of {@link #describe()}.
 * Subclasses must implement {@link #sound()} and {@link #accept(IVisitor)}.
 */
public abstract class Animal implements IAnimal {

    protected final String name;
    protected final int age;
    protected final String species;
    protected final double weight;

    protected Animal(String name, int age, String species, double weight) {
        this.name = name;
        this.age = age;
        this.species = species;
        this.weight = weight;
    }

    @Override public String getName()    { return name; }
    @Override public int getAge()       { return age; }
    @Override public String getSpecies(){ return species; }
    @Override public double getWeight() { return weight; }

    @Override
    public String describe() {
        return String.format("%s (%s), age=%d, weight=%.1fkg, sound='%s'",
                name, species, age, weight, sound());
    }

    @Override
    public String toString() { return describe(); }
}
