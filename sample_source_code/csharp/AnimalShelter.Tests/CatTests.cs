using Embold.Sample;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Embold.Sample.Tests;

[TestClass]
public class CatTests
{
    private readonly Cat _indoorCat  = new("Whiskers", 3, isIndoor: true,   4.5);
    private readonly Cat _outdoorCat = new("Shadow",   5, isIndoor: false,  7.0);

    // ── Properties ──────────────────────────────────────────────────────────

    [TestMethod]
    public void Sound_ReturnsMeow()
        => Assert.AreEqual("Meow", _indoorCat.Sound());

    [TestMethod]
    public void IsIndoor_ReflectsConstructorFlag()
    {
        Assert.IsTrue(_indoorCat.IsIndoor);
        Assert.IsFalse(_outdoorCat.IsIndoor);
    }

    [TestMethod]
    public void Name_ReturnsConstructorValue()
        => Assert.AreEqual("Whiskers", _indoorCat.Name);

    [TestMethod]
    public void Age_ReturnsConstructorValue()
        => Assert.AreEqual(3, _indoorCat.Age);

    [TestMethod]
    public void Species_ReturnsFeliscatus()
        => Assert.AreEqual("Felis catus", _indoorCat.Species);

    [TestMethod]
    public void Weight_ReturnsConstructorValue()
        => Assert.AreEqual(4.5, _indoorCat.Weight);

    [TestMethod]
    public void Describe_ContainsNameAndSound()
    {
        var desc = _indoorCat.Describe();
        StringAssert.Contains(desc, "Whiskers");
        StringAssert.Contains(desc, "Meow");
    }

    // ── ExpectedLifespan ─────────────────────────────────────────────────────

    [TestMethod]
    public void ExpectedLifespan_Indoor_GreaterThanOutdoor()
    {
        var indoor  = new Cat("A", 2, isIndoor: true,  4.5);
        var outdoor = new Cat("B", 2, isIndoor: false, 4.5);
        Assert.IsTrue(indoor.ExpectedLifespan() > outdoor.ExpectedLifespan());
    }

    [TestMethod]
    public void ExpectedLifespan_IndoorAtIdealWeight_Returns15()
    {
        // base=15, deviation=0 → adjustment=0
        var cat = new Cat("C", 3, isIndoor: true, 4.5);
        Assert.AreEqual(15, cat.ExpectedLifespan());
    }

    [TestMethod]
    public void ExpectedLifespan_OutdoorAtIdealWeight_Returns10()
    {
        // base=10, deviation=0 → adjustment=0
        var cat = new Cat("D", 2, isIndoor: false, 4.5);
        Assert.AreEqual(10, cat.ExpectedLifespan());
    }

    [TestMethod]
    public void ExpectedLifespan_HeavyOutdoorCat_AtLeastFive()
    {
        var heavy = new Cat("Fat", 10, isIndoor: false, 20.0);
        Assert.IsTrue(heavy.ExpectedLifespan() >= 5);
    }

    // ── Visitor double-dispatch ──────────────────────────────────────────────

    [TestMethod]
    public void Accept_IncrementsCatCountOnVisitor()
    {
        var visitor = new AnimalVisitor();
        _indoorCat.Accept(visitor);
        Assert.AreEqual(1, visitor.CatCount);
        Assert.AreEqual(0, visitor.DogCount);
    }

    [TestMethod]
    public void Accept_MultipleCats_CountsCorrectly()
    {
        var visitor = new AnimalVisitor();
        _indoorCat.Accept(visitor);
        _outdoorCat.Accept(visitor);
        Assert.AreEqual(2, visitor.CatCount);
        Assert.AreEqual(0, visitor.DogCount);
    }
}
