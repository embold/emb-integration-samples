namespace Embold.Sample;

/// <summary>
/// Generates a formatted shelter summary using <see cref="AnimalVisitor"/>.
/// Orchestrates calls to Shelter, AnimalVisitor, and MathUtils.
/// </summary>
public sealed class Report
{
    private readonly Shelter _shelter;

    public Report(Shelter shelter) => _shelter = shelter;

    public string Generate()
    {
        var visitor = new AnimalVisitor();
        foreach (var animal in _shelter.Animals)
            animal.Accept(visitor);

        double[] ages      = _shelter.Animals.Select(a => (double)a.Age).ToArray();
        double   ageStdDev = MathUtils.StandardDeviation(ages);

        var sb = new System.Text.StringBuilder();
        sb.AppendLine($"=== Shelter Report: {_shelter.Name} ===");
        sb.AppendLine($"Total animals   : {visitor.TotalCount}");
        sb.AppendLine($"Dogs            : {visitor.DogCount}");
        sb.AppendLine($"Cats            : {visitor.CatCount}");
        sb.AppendLine($"Avg health score: {visitor.AverageHealthScore():F2}%");
        sb.AppendLine($"Avg age         : {_shelter.AverageAge():F1} years");
        sb.AppendLine($"Age std-dev     : {ageStdDev:F2}");
        sb.AppendLine($"Available cap   : {_shelter.AvailableCapacity()}");
        sb.AppendLine("--- Animal Details ---");
        foreach (var a in _shelter.Animals)
            sb.AppendLine($"  {a.Describe()}");
        return sb.ToString();
    }
}
