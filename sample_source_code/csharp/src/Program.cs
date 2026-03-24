using Embold.Sample;

// Entry point: wires together all classes
var shelter = new Shelter("Happy Paws", 20);

shelter.Intake(new Dog("Buddy",    3, "Labrador",        28.5));
shelter.Intake(new Dog("Max",      7, "German Shepherd", 33.0));
shelter.Intake(new Dog("Tiny",     1, "Chihuahua",        2.1));
shelter.Intake(new Cat("Whiskers", 5, isIndoor: true,    4.2));
shelter.Intake(new Cat("Shadow",  10, isIndoor: false,   5.8));
shelter.Intake(new Cat("Luna",     2, isIndoor: true,    3.9));

Console.Write(new Report(shelter).Generate());

Console.WriteLine($"\nfibonacci(10) = {MathUtils.Fibonacci(10)}");
Console.WriteLine($"factorial(6)  = {MathUtils.Factorial(6)}");

Console.WriteLine("\nAdopting 'Max'...");
var adopted = shelter.Adopt("Max");
Console.WriteLine(adopted is not null ? $"  Adopted: {adopted.Name}" : "  Not found.");

Console.WriteLine("\nPost-adoption report:");
Console.Write(new Report(shelter).Generate());
