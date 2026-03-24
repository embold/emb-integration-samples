namespace Embold.Sample;

/// <summary>
/// Abstract base class implementing IAnimal.
/// Provides shared state; subclasses override <see cref="Sound"/> and <see cref="Accept"/>.
/// </summary>
public abstract class Animal : IAnimal
{
    protected Animal(string name, int age, string species, double weight)
    {
        Name    = name;
        Age     = age;
        Species = species;
        Weight  = weight;
    }

    public string Name    { get; }
    public string Species { get; }
    public int    Age     { get; }
    public double Weight  { get; }

    public abstract string Sound();
    public abstract void   Accept(IVisitor visitor);

    public virtual string Describe() =>
        $"{Name} ({Species}), age={Age}, weight={Weight:F1}kg, sound='{Sound()}'";

    public override string ToString() => Describe();
}
