namespace Embold.Sample;

/// <summary>Concrete Dog class with breed-specific health scoring.</summary>
public sealed class Dog : Animal
{
    private static readonly Dictionary<string, double> BreedIdealWeight =
        new(StringComparer.OrdinalIgnoreCase)
        {
            ["chihuahua"]       = 2.5,
            ["labrador"]        = 30.0,
            ["german shepherd"] = 35.0,
        };

    public Dog(string name, int age, string breed, double weight)
        : base(name, age, "Canis lupus familiaris", weight)
    {
        Breed = breed;
    }

    public string Breed { get; }

    public override string Sound() => "Woof";
    public override void   Accept(IVisitor visitor) => visitor.Visit(this);

    /// <summary>
    /// Computes a health score (0–100) based on age and weight vs breed ideal.
    /// Outgoing calls to <see cref="MathUtils"/>.
    /// </summary>
    public double ComputeHealthScore()
    {
        double score = 100.0;

        score -= Age switch
        {
            <= 2 => MathUtils.LinearDecay(Age, 2,  5),
            <= 7 => MathUtils.LinearDecay(Age, 7,  10),
            _    => MathUtils.LinearDecay(Age, 15, 50),
        };

        double idealWeight = BreedIdealWeight.GetValueOrDefault(Breed, 15.0);
        double deviation   = Math.Abs(Weight - idealWeight) / idealWeight;
        score -= deviation * 20;

        return MathUtils.Clamp(Math.Round(score, 2), 0, 100);
    }
}
