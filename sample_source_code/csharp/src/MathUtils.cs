namespace Embold.Sample;

/// <summary>
/// Static math helpers used across the codebase.
/// Called by Dog, Cat, AnimalVisitor, and Report.
/// </summary>
public static class MathUtils
{
    public static double Clamp(double value, double min, double max) =>
        Math.Max(min, Math.Min(max, value));

    /// <summary>Returns how much to subtract given current progress toward max.</summary>
    public static double LinearDecay(double current, double max, double maxPenalty)
    {
        if (max <= 0) return 0;
        return Clamp(current / max * maxPenalty, 0, maxPenalty);
    }

    /// <summary>Iterative Fibonacci.</summary>
    public static long Fibonacci(int n)
    {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) (a, b) = (b, a + b);
        return b;
    }

    /// <summary>Population standard deviation.</summary>
    public static double StandardDeviation(IEnumerable<double> values)
    {
        var list = values.ToList();
        if (list.Count == 0) return 0;
        double mean     = list.Average();
        double variance = list.Average(v => (v - mean) * (v - mean));
        return Math.Sqrt(variance);
    }

    /// <summary>Iterative factorial.</summary>
    public static long Factorial(int n)
    {
        if (n < 0) throw new ArgumentException("Negative input", nameof(n));
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
}
