namespace Embold.Sample;

/// <summary>Interface defining the contract for all animals.</summary>
public interface IAnimal
{
    string Name    { get; }
    string Species { get; }
    int    Age     { get; }
    double Weight  { get; }

    string Sound();
    string Describe();

    /// <summary>Accept a visitor — enables double-dispatch.</summary>
    void Accept(IVisitor visitor);
}
