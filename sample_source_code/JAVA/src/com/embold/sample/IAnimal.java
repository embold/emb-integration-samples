package com.embold.sample;

/**
 * Interface defining the contract for all animals.
 * Demonstrates interface usage and polymorphism.
 */
public interface IAnimal {

    String getName();

    String getSpecies();

    int getAge();

    double getWeight();

    /** Sound the animal makes. */
    String sound();

    /** Human-readable one-line description. */
    String describe();

    /**
     * Accept a visitor — enables double-dispatch (Visitor pattern).
     *
     * @param visitor the visitor to accept
     */
    void accept(IVisitor visitor);
}
