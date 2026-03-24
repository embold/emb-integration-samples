package com.embold.sample;

/**
 * Application entry point.
 * Wires together all classes: Shelter (composition), Dog/Cat (inheritance),
 * Report (orchestration), and MathUtils (utilities).
 */
public class Main {

    public static void main(String[] args) {
        Shelter shelter = new Shelter("Happy Paws", 20);

        shelter.intake(new Dog("Buddy",   3, "Labrador",        28.5));
        shelter.intake(new Dog("Max",     7, "German Shepherd", 33.0));
        shelter.intake(new Dog("Tiny",    1, "Chihuahua",        2.1));
        shelter.intake(new Cat("Whiskers", 5, true,  4.2));
        shelter.intake(new Cat("Shadow",  10, false, 5.8));
        shelter.intake(new Cat("Luna",    2, true,   3.9));

        System.out.println(new Report(shelter).generate());

        System.out.println("fibonacci(10) = " + MathUtils.fibonacci(10));
        System.out.println("factorial(6)  = " + MathUtils.factorial(6));

        System.out.println("\nAdopting 'Max'...");
        shelter.adopt("Max").ifPresentOrElse(
                a -> System.out.println("  Adopted: " + a.getName()),
                () -> System.out.println("  Not found.")
        );

        System.out.println("\nPost-adoption report:");
        System.out.println(new Report(shelter).generate());
    }
}
