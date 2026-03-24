using Embold.Sample;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Embold.Sample.Tests;

[TestClass]
public class ShelterTests
{
    private Shelter _shelter = null!;
    private Dog _rex         = null!;
    private Cat _whiskers    = null!;

    [TestInitialize]
    public void SetUp()
    {
        _shelter  = new Shelter("Happy Paws", 5);
        _rex      = new Dog("Rex",      3, "Labrador", 30.0);
        _whiskers = new Cat("Whiskers", 2, isIndoor: true, 4.5);
    }

    // ── Name ────────────────────────────────────────────────────────────────

    [TestMethod]
    public void Name_ReturnsConstructorValue()
        => Assert.AreEqual("Happy Paws", _shelter.Name);

    // ── Intake ──────────────────────────────────────────────────────────────

    [TestMethod]
    public void Intake_UnderCapacity_ReturnsTrue()
        => Assert.IsTrue(_shelter.Intake(_rex));

    [TestMethod]
    public void Intake_AddsAnimalToList()
    {
        _shelter.Intake(_rex);
        Assert.AreEqual(1, _shelter.Animals.Count);
    }

    [TestMethod]
    public void Intake_AtCapacity_ReturnsFalse()
    {
        var tiny = new Shelter("Tiny", 1);
        tiny.Intake(_rex);
        Assert.IsFalse(tiny.Intake(_whiskers));
    }

    [TestMethod]
    public void Intake_AtCapacity_DoesNotAddAnimal()
    {
        var tiny = new Shelter("Tiny", 1);
        tiny.Intake(_rex);
        tiny.Intake(_whiskers);
        Assert.AreEqual(1, tiny.Animals.Count);
    }

    [TestMethod]
    public void Intake_MultipleAnimals_AllAdded()
    {
        _shelter.Intake(_rex);
        _shelter.Intake(_whiskers);
        Assert.AreEqual(2, _shelter.Animals.Count);
    }

    // ── Adopt ────────────────────────────────────────────────────────────────

    [TestMethod]
    public void Adopt_ExistingAnimal_ReturnsAnimal()
    {
        _shelter.Intake(_rex);
        var adopted = _shelter.Adopt("Rex");
        Assert.IsNotNull(adopted);
        Assert.AreEqual("Rex", adopted.Name);
    }

    [TestMethod]
    public void Adopt_ExistingAnimal_RemovesFromShelter()
    {
        _shelter.Intake(_rex);
        _shelter.Adopt("Rex");
        Assert.AreEqual(0, _shelter.Animals.Count);
    }

    [TestMethod]
    public void Adopt_CaseInsensitive_Works()
    {
        _shelter.Intake(_rex);
        Assert.IsNotNull(_shelter.Adopt("rex"));
    }

    [TestMethod]
    public void Adopt_UnknownName_ReturnsNull()
    {
        _shelter.Intake(_rex);
        Assert.IsNull(_shelter.Adopt("Unknown"));
    }

    [TestMethod]
    public void Adopt_UnknownName_DoesNotRemoveOtherAnimals()
    {
        _shelter.Intake(_rex);
        _shelter.Adopt("Unknown");
        Assert.AreEqual(1, _shelter.Animals.Count);
    }

    // ── FindBySpecies ────────────────────────────────────────────────────────

    [TestMethod]
    public void FindBySpecies_ReturnsMatchingAnimals()
    {
        _shelter.Intake(_rex);
        _shelter.Intake(_whiskers);
        var cats = _shelter.FindBySpecies("Felis catus").ToList();
        Assert.AreEqual(1, cats.Count);
        Assert.AreEqual("Whiskers", cats[0].Name);
    }

    [TestMethod]
    public void FindBySpecies_CaseInsensitive_Works()
    {
        _shelter.Intake(_rex);
        Assert.AreEqual(1, _shelter.FindBySpecies("CANIS LUPUS FAMILIARIS").Count());
    }

    [TestMethod]
    public void FindBySpecies_NoMatch_ReturnsEmpty()
    {
        _shelter.Intake(_rex);
        Assert.AreEqual(0, _shelter.FindBySpecies("Felis catus").Count());
    }

    // ── AvailableCapacity ────────────────────────────────────────────────────

    [TestMethod]
    public void AvailableCapacity_EmptyShelter_EqualsCapacity()
        => Assert.AreEqual(5, _shelter.AvailableCapacity());

    [TestMethod]
    public void AvailableCapacity_AfterOneIntake_Decreases()
    {
        _shelter.Intake(_rex);
        Assert.AreEqual(4, _shelter.AvailableCapacity());
    }

    [TestMethod]
    public void AvailableCapacity_AfterAdopt_Increases()
    {
        _shelter.Intake(_rex);
        _shelter.Adopt("Rex");
        Assert.AreEqual(5, _shelter.AvailableCapacity());
    }

    // ── AverageAge ───────────────────────────────────────────────────────────

    [TestMethod]
    public void AverageAge_EmptyShelter_ReturnsZero()
        => Assert.AreEqual(0.0, _shelter.AverageAge());

    [TestMethod]
    public void AverageAge_SingleAnimal_ReturnsItsAge()
    {
        _shelter.Intake(_rex);    // age 3
        Assert.AreEqual(3.0, _shelter.AverageAge(), 1e-9);
    }

    [TestMethod]
    public void AverageAge_MultipleAnimals_IsCorrect()
    {
        _shelter.Intake(_rex);       // age 3
        _shelter.Intake(_whiskers);  // age 2
        Assert.AreEqual(2.5, _shelter.AverageAge(), 1e-9);
    }

    // ── Animals is read-only ─────────────────────────────────────────────────

    [TestMethod]
    [ExpectedException(typeof(NotSupportedException))]
    public void Animals_IsReadOnlyList_ThrowsOnMutation()
    {
        _shelter.Intake(_rex);
        ((System.Collections.Generic.IList<IAnimal>)_shelter.Animals).Add(_whiskers);
    }
}
