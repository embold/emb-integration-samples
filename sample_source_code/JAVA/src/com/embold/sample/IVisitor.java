package com.embold.sample;

/**
 * Visitor interface for operations on Animal types.
 * Enables adding new operations to the class hierarchy without modifying existing classes.
 */
public interface IVisitor {

    /** Called when visiting a Dog instance. */
    void visit(Dog dog);

    /** Called when visiting a Cat instance. */
    void visit(Cat cat);
}
