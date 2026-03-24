package com.embold.sample;

import java.util.List;

/**
 * Report generates a formatted summary of a {@link Shelter} using {@link AnimalVisitor}.
 * Demonstrates orchestration: uses Shelter (composition) and AnimalVisitor (visitor pattern).
 * Incoming calls from Main; outgoing calls to Shelter, AnimalVisitor, and MathUtils.
 */
public class Report {

    private final Shelter shelter;

    public Report(Shelter shelter) {
        this.shelter = shelter;
    }

    /** Runs the visitor over all animals and formats the result. */
    public String generate() {
        AnimalVisitor visitor = new AnimalVisitor();
        List<IAnimal> animals = shelter.getAnimals();

        // Drive the visitor pattern
        for (IAnimal animal : animals) {
            animal.accept(visitor);
        }

        double[] ages = animals.stream().mapToDouble(IAnimal::getAge).toArray();
        double ageStdDev = MathUtils.standardDeviation(ages);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Shelter Report: ").append(shelter.getName()).append(" ===\n");
        sb.append(String.format("Total animals   : %d%n", visitor.getTotalCount()));
        sb.append(String.format("Dogs            : %d%n", visitor.getDogCount()));
        sb.append(String.format("Cats            : %d%n", visitor.getCatCount()));
        sb.append(String.format("Avg health score: %.2f%%%n", visitor.getAverageHealthScore()));
        sb.append(String.format("Avg age         : %.1f years%n", shelter.averageAge()));
        sb.append(String.format("Age std-dev     : %.2f%n", ageStdDev));
        sb.append(String.format("Available cap   : %d%n", shelter.availableCapacity()));
        sb.append("--- Animal Details ---\n");
        for (IAnimal a : animals) {
            sb.append("  ").append(a.describe()).append('\n');
        }
        return sb.toString();
    }
}
