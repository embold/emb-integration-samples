namespace Embold.Sample;

/// <summary>Concrete Cat class with lifespan estimation.</summary>
public sealed class Cat : Animal
{
    public Cat(string name, int age, bool isIndoor, double weight)
        : base(name, age, "Felis catus", weight)
    {
        IsIndoor = isIndoor;
    }

    public bool IsIndoor { get; }

    public override string Sound() => "Meow";
    public override void   Accept(IVisitor visitor) => visitor.Visit(this);

    /// <summary>Returns expected lifespan adjusted for environment and weight.</summary>
    public int ExpectedLifespan()
    {
        int    baseLine   = IsIndoor ? 15 : 10;
        double deviation  = Math.Abs(Weight - 4.5) / 4.5;
        int    adjustment = (int)MathUtils.LinearDecay(deviation, 1.0, 5);
        return Math.Max(5, baseLine - adjustment);
    }
}
