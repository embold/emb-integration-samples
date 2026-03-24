package com.embold.sample;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Visitor: accumulates health stats while traversing animals.
 * Demonstrates double-dispatch — each animal type calls a different overload.
 * Incoming calls from Dog.accept() and Cat.accept().
 */
public class AnimalVisitor implements IVisitor {

    private int dogCount = 0;
    private int catCount = 0;
    private double totalHealthScore = 0.0;
    private final Map<String, Integer> speciesCount = new HashMap<>();

    @Override
    public void visit(Dog dog) {
        dogCount++;
        totalHealthScore += dog.computeHealthScore();
        speciesCount.merge("Dog", 1, Integer::sum);
    }

    @Override
    public void visit(Cat cat) {
        catCount++;
        // Proxy health score for cats via weight deviation
        double proxy = MathUtils.clamp(100 - Math.abs(cat.getWeight() - 4.5) * 10, 0, 100);
        totalHealthScore += proxy;
        speciesCount.merge("Cat", 1, Integer::sum);
    }

    public int getDogCount()  { return dogCount; }
    public int getCatCount()  { return catCount; }
    public int getTotalCount(){ return dogCount + catCount; }

    public double getAverageHealthScore() {
        int total = getTotalCount();
        return total == 0 ? 0.0 : MathUtils.roundToDecimalPlaces(totalHealthScore / total, 2);
    }

    public Map<String, Integer> getSpeciesCount() { return Map.copyOf(speciesCount); }

    public void reset() {
        dogCount = 0;
        catCount = 0;
        totalHealthScore = 0.0;
        speciesCount.clear();
    }
}
