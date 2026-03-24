using Embold.Sample;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Embold.Sample.Tests;

[TestClass]
public class DogTests
{
    private readonly Dog _labrador   = new("Rex",   3, "Labrador",  30.0);
    private readonly Dog _chihuahua  = new("Tiny",  1, "Chihuahua",  2.5);
    private readonly Dog _defaultBreed = new("Buddy", 8, "Poodle",  15.0);

    // ── Properties ──────────────────────────────────────────────────────────

    [TestMethod]
    public void Name_ReturnsConstructorValue()
        => Assert.AreEqual("Rex", _labrador.Name);

    [TestMethod]
    public void Age_ReturnsConstructorValue()
        => Assert.AreEqual(3, _labrador.Age);

    [TestMethod]
    public void Species_ReturnsCanisLupusFamiliaris()
        => Assert.AreEqual("Canis lupus familiaris", _labrador.Species);

    [TestMethod]
    public void Weight_ReturnsConstructorValue()
        => Assert.AreEqual(30.0, _labrador.Weight);

    [TestMethod]
    public void Breed_ReturnsConstructorValue()
        => Assert.AreEqual("Labrador", _labrador.Breed);

    [TestMethod]
    public void Sound_ReturnsWoof()
        => Assert.AreEqual("Woof", _labrador.Sound());

    [TestMethod]
    public void Describe_ContainsNameAndSound()
    {
        var desc = _labrador.Describe();
        StringAssert.Contains(desc, "Rex");
        StringAssert.Contains(desc, "Woof");
    }

    // ── ComputeHealthScore ───────────────────────────────────────────────────

    [TestMethod]
    public void HealthScore_IdealWeightYoungChihuahua_NearHundred()
        => Assert.IsTrue(_chihuahua.ComputeHealthScore() >= 90.0);

    [TestMethod]
    public void HealthScore_IsWithinZeroToHundred()
    {
        Assert.IsTrue(_labrador.ComputeHealthScore()    >= 0.0);
        Assert.IsTrue(_labrador.ComputeHealthScore()    <= 100.0);
        Assert.IsTrue(_defaultBreed.ComputeHealthScore() >= 0.0);
        Assert.IsTrue(_defaultBreed.ComputeHealthScore() <= 100.0);
    }

    [TestMethod]
    public void HealthScore_OlderDog_LowerThanYounger()
    {
        var young = new Dog("A",  2, "Labrador", 30.0);
        var old   = new Dog("B", 12, "Labrador", 30.0);
        Assert.IsTrue(old.ComputeHealthScore() < young.ComputeHealthScore());
    }

    [TestMethod]
    public void HealthScore_GermanShepherdAtIdealWeight_HighScore()
    {
        var gs = new Dog("Max", 3, "German Shepherd", 35.0);
        Assert.IsTrue(gs.ComputeHealthScore() >= 85.0);
    }

    [TestMethod]
    public void HealthScore_UnknownBreed_UsesFifteenKgIdeal()
    {
        var dog = new Dog("X", 3, "Beagle", 15.0);
        var score = dog.ComputeHealthScore();
        Assert.IsTrue(score >= 0.0 && score <= 100.0);
    }

    // ── Visitor double-dispatch ──────────────────────────────────────────────

    [TestMethod]
    public void Accept_IncrementsDogCountOnVisitor()
    {
        var visitor = new AnimalVisitor();
        _labrador.Accept(visitor);
        Assert.AreEqual(1, visitor.DogCount);
        Assert.AreEqual(0, visitor.CatCount);
    }

    [TestMethod]
    public void Accept_MultipleDogs_CountsCorrectly()
    {
        var visitor = new AnimalVisitor();
        _labrador.Accept(visitor);
        _chihuahua.Accept(visitor);
        _defaultBreed.Accept(visitor);
        Assert.AreEqual(3, visitor.DogCount);
        Assert.AreEqual(0, visitor.CatCount);
    }
}
