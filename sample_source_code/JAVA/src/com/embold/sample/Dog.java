package com.embold.sample;

/**
 * Concrete Dog class – inherits from Animal.
 * Demonstrates method overriding, breed-specific logic, and outgoing calls to MathUtils.
 */
public class Dog extends Animal {

    private final String breed;

    public Dog(String name, int age, String breed, double weight) {
        super(name, age, "Canis lupus familiaris", weight);
        this.breed = breed;
    }

    public String getBreed() { return breed; }

    @Override
    public String sound() { return "Woof"; }

    /** Double-dispatch: calls visitor.visit(this). */
    @Override
    public void accept(IVisitor visitor) { visitor.visit(this); }

    /**
     * Computes a health score (0–100) using age and weight heuristics.
     * Demonstrates a method with multiple branches and outgoing calls.
     *
     * @return health score
     */
    public double computeHealthScore() {
        double score = 100.0;

        // Age penalty increases non-linearly after age 7
        if (age <= 2) {
            score -= MathUtils.linearDecay(age, 2, 5);
        } else if (age <= 7) {
            score -= MathUtils.linearDecay(age, 7, 10);
        } else {
            score -= MathUtils.linearDecay(age, 15, 50);
        }

        // Weight deviation from breed ideal
        double idealWeight = estimateIdealWeight();
        double deviation = Math.abs(weight - idealWeight) / idealWeight;
        score -= deviation * 20;

        return MathUtils.clamp(MathUtils.roundToDecimalPlaces(score, 2), 0.0, 100.0);
    }

    private double estimateIdealWeight() {
        return switch (breed.toLowerCase()) {
            case "chihuahua"        -> 2.5;
            case "labrador"         -> 30.0;
            case "german shepherd"  -> 35.0;
            default                 -> 15.0;
        };
    }
}
