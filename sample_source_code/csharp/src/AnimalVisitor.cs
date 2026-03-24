namespace Embold.Sample;

/// <summary>
/// Concrete visitor: accumulates health stats while traversing animals.
/// Demonstrates double-dispatch — each animal type routes to a separate overload.
/// </summary>
public sealed class AnimalVisitor : IVisitor
{
    private int    _dogCount;
    private int    _catCount;
    private double _totalHealth;

    public void Visit(Dog dog)
    {
        _dogCount++;
        _totalHealth += dog.ComputeHealthScore();
    }

    public void Visit(Cat cat)
    {
        _catCount++;
        double proxy = MathUtils.Clamp(100 - Math.Abs(cat.Weight - 4.5) * 10, 0, 100);
        _totalHealth += proxy;
    }

    public int    DogCount   => _dogCount;
    public int    CatCount   => _catCount;
    public int    TotalCount => _dogCount + _catCount;

    public double AverageHealthScore() =>
        TotalCount == 0 ? 0.0 : Math.Round(_totalHealth / TotalCount, 2);
}
