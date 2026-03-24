using Embold.Sample;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Embold.Sample.Tests;

[TestClass]
public class MathUtilsTests
{
    // ── Clamp ────────────────────────────────────────────────────────────────

    [TestMethod]
    public void Clamp_BelowMin_ReturnsMin()
        => Assert.AreEqual(0.0, MathUtils.Clamp(-5.0, 0.0, 10.0));

    [TestMethod]
    public void Clamp_AboveMax_ReturnsMax()
        => Assert.AreEqual(10.0, MathUtils.Clamp(15.0, 0.0, 10.0));

    [TestMethod]
    public void Clamp_WithinRange_ReturnsValue()
        => Assert.AreEqual(5.5, MathUtils.Clamp(5.5, 0.0, 10.0));

    [TestMethod]
    public void Clamp_AtMinBoundary_ReturnsMin()
        => Assert.AreEqual(0.0, MathUtils.Clamp(0.0, 0.0, 10.0));

    [TestMethod]
    public void Clamp_AtMaxBoundary_ReturnsMax()
        => Assert.AreEqual(10.0, MathUtils.Clamp(10.0, 0.0, 10.0));

    // ── LinearDecay ──────────────────────────────────────────────────────────

    [TestMethod]
    public void LinearDecay_ZeroMax_ReturnsZero()
        => Assert.AreEqual(0.0, MathUtils.LinearDecay(5.0, 0.0, 10.0));

    [TestMethod]
    public void LinearDecay_HalfProgress_ReturnsHalfPenalty()
        => Assert.AreEqual(5.0, MathUtils.LinearDecay(5.0, 10.0, 10.0), 1e-9);

    [TestMethod]
    public void LinearDecay_OverMax_ClampsToMaxPenalty()
        => Assert.AreEqual(10.0, MathUtils.LinearDecay(20.0, 10.0, 10.0));

    [TestMethod]
    public void LinearDecay_ZeroProgress_ReturnsZero()
        => Assert.AreEqual(0.0, MathUtils.LinearDecay(0.0, 10.0, 10.0));

    // ── Fibonacci ────────────────────────────────────────────────────────────

    [DataTestMethod]
    [DataRow(0,  0L)]
    [DataRow(1,  1L)]
    [DataRow(2,  1L)]
    [DataRow(6,  8L)]
    [DataRow(10, 55L)]
    public void Fibonacci_KnownValues(int n, long expected)
        => Assert.AreEqual(expected, MathUtils.Fibonacci(n));

    // ── StandardDeviation ────────────────────────────────────────────────────

    [TestMethod]
    public void StandardDeviation_EmptyCollection_ReturnsZero()
        => Assert.AreEqual(0.0, MathUtils.StandardDeviation([]));

    [TestMethod]
    public void StandardDeviation_UniformValues_ReturnsZero()
        => Assert.AreEqual(0.0, MathUtils.StandardDeviation([4.0, 4.0, 4.0]), 1e-9);

    [TestMethod]
    public void StandardDeviation_KnownValues_ReturnsTwo()
    {
        // population std-dev of {2,4,4,4,5,5,7,9} == 2.0
        double result = MathUtils.StandardDeviation([2, 4, 4, 4, 5, 5, 7, 9]);
        Assert.AreEqual(2.0, result, 1e-9);
    }

    [TestMethod]
    public void StandardDeviation_SingleElement_ReturnsZero()
        => Assert.AreEqual(0.0, MathUtils.StandardDeviation([42.0]));

    // ── Factorial ────────────────────────────────────────────────────────────

    [TestMethod]
    public void Factorial_Zero_ReturnsOne()
        => Assert.AreEqual(1L, MathUtils.Factorial(0));

    [TestMethod]
    public void Factorial_One_ReturnsOne()
        => Assert.AreEqual(1L, MathUtils.Factorial(1));

    [TestMethod]
    public void Factorial_Five_Returns120()
        => Assert.AreEqual(120L, MathUtils.Factorial(5));

    [TestMethod]
    [ExpectedException(typeof(ArgumentException))]
    public void Factorial_Negative_ThrowsArgumentException()
        => MathUtils.Factorial(-1);
}
