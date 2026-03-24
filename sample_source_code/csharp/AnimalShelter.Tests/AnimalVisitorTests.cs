using Embold.Sample;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Embold.Sample.Tests;

[TestClass]
public class AnimalVisitorTests
{
    private AnimalVisitor _visitor  = null!;
    private Dog           _labrador = null!;
    private Cat           _indoorCat = null!;

    [TestInitialize]
    public void SetUp()
    {
        _visitor   = new AnimalVisitor();
        _labrador  = new Dog("Rex",      3, "Labrador", 30.0);
        _indoorCat = new Cat("Whiskers", 2, isIndoor: true, 4.5);
    }

    // ── Initial state ────────────────────────────────────────────────────────

    [TestMethod]
    public void Initial_DogCount_IsZero()
        => Assert.AreEqual(0, _visitor.DogCount);

    [TestMethod]
    public void Initial_CatCount_IsZero()
        => Assert.AreEqual(0, _visitor.CatCount);

    [TestMethod]
    public void Initial_TotalCount_IsZero()
        => Assert.AreEqual(0, _visitor.TotalCount);

    [TestMethod]
    public void Initial_AverageHealthScore_IsZero()
        => Assert.AreEqual(0.0, _visitor.AverageHealthScore());

    // ── Visit(Dog) ───────────────────────────────────────────────────────────

    [TestMethod]
    public void VisitDog_IncrementsDogCount()
    {
        _visitor.Visit(_labrador);
        Assert.AreEqual(1, _visitor.DogCount);
        Assert.AreEqual(0, _visitor.CatCount);
    }

    [TestMethod]
    public void VisitDog_UpdatesTotalCount()
    {
        _visitor.Visit(_labrador);
        Assert.AreEqual(1, _visitor.TotalCount);
    }

    [TestMethod]
    public void VisitDog_AverageHealthScore_IsPositive()
    {
        _visitor.Visit(_labrador);
        Assert.IsTrue(_visitor.AverageHealthScore() > 0.0);
    }

    // ── Visit(Cat) ───────────────────────────────────────────────────────────

    [TestMethod]
    public void VisitCat_IncrementsCatCount()
    {
        _visitor.Visit(_indoorCat);
        Assert.AreEqual(1, _visitor.CatCount);
        Assert.AreEqual(0, _visitor.DogCount);
    }

    [TestMethod]
    public void VisitCat_AtIdealWeight_HealthScoreIs100()
    {
        // weight=4.5 == ideal → proxy = 100 - |4.5-4.5|*10 = 100
        _visitor.Visit(_indoorCat);
        Assert.AreEqual(100.0, _visitor.AverageHealthScore(), 1e-6);
    }

    // ── Mixed visits ─────────────────────────────────────────────────────────

    [TestMethod]
    public void MixedVisits_CountsAreCorrect()
    {
        _visitor.Visit(_labrador);
        _visitor.Visit(_indoorCat);
        _visitor.Visit(new Dog("Max", 5, "Poodle", 15.0));

        Assert.AreEqual(2, _visitor.DogCount);
        Assert.AreEqual(1, _visitor.CatCount);
        Assert.AreEqual(3, _visitor.TotalCount);
    }

    [TestMethod]
    public void AverageHealthScore_AfterMixed_IsWithinRange()
    {
        _visitor.Visit(_labrador);
        _visitor.Visit(_indoorCat);
        var avg = _visitor.AverageHealthScore();
        Assert.IsTrue(avg >= 0.0 && avg <= 100.0);
    }

    // ── Double-dispatch via Accept ────────────────────────────────────────────

    [TestMethod]
    public void DogAccept_UsesVisitDog()
    {
        _labrador.Accept(_visitor);
        Assert.AreEqual(1, _visitor.DogCount);
    }

    [TestMethod]
    public void CatAccept_UsesVisitCat()
    {
        _indoorCat.Accept(_visitor);
        Assert.AreEqual(1, _visitor.CatCount);
    }
}
