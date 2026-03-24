package com.embold.sample;

/**
 * Concrete Cat class – inherits from Animal.
 * Demonstrates environment-based lifespan calculation.
 */
public class Cat extends Animal {

    private final boolean indoor;

    public Cat(String name, int age, boolean indoor, double weight) {
        super(name, age, "Felis catus", weight);
        this.indoor = indoor;
    }

    public boolean isIndoor() { return indoor; }

    @Override
    public String sound() { return "Meow"; }

    @Override
    public void accept(IVisitor visitor) { visitor.visit(this); }

    /**
     * Returns expected lifespan based on environment and weight.
     * Outgoing call to {@link MathUtils#linearDecay}.
     */
    public int expectedLifespan() {
        int base = indoor ? 15 : 10;
        double deviation = Math.abs(weight - 4.5) / 4.5;
        int adjustment = (int) MathUtils.linearDecay(deviation, 1.0, 5);
        return Math.max(5, base - adjustment);
    }
}
