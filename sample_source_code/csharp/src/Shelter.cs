namespace Embold.Sample;

/// <summary>
/// Shelter holds a collection of <see cref="IAnimal"/> instances.
/// Demonstrates composition (has-a relationship).
/// </summary>
public sealed class Shelter
{
    private readonly List<IAnimal> _animals = [];

    public Shelter(string name, int capacity)
    {
        Name     = name;
        Capacity = capacity;
    }

    public string Name     { get; }
    public int    Capacity { get; }

    /// <summary>Adds an animal; returns <c>false</c> if at capacity.</summary>
    public bool Intake(IAnimal animal)
    {
        if (_animals.Count >= Capacity) return false;
        _animals.Add(animal);
        return true;
    }

    /// <summary>Removes and returns the named animal, or <c>null</c> if not found.</summary>
    public IAnimal? Adopt(string animalName)
    {
        var found = _animals.FirstOrDefault(
            a => string.Equals(a.Name, animalName, StringComparison.OrdinalIgnoreCase));
        if (found is not null) _animals.Remove(found);
        return found;
    }

    public IReadOnlyList<IAnimal> Animals => _animals.AsReadOnly();

    public int    AvailableCapacity() => Capacity - _animals.Count;
    public double AverageAge()        => _animals.Count == 0 ? 0.0 : _animals.Average(a => (double)a.Age);

    public IEnumerable<IAnimal> FindBySpecies(string species) =>
        _animals.Where(a => string.Equals(a.Species, species, StringComparison.OrdinalIgnoreCase));
}
