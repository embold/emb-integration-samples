package com.embold.sample;

/**
 * Utility class with standalone mathematical helpers.
 * Demonstrates static methods, recursion (Fibonacci), and statistical computation.
 * Called by Dog, Cat, AnimalVisitor, and Report.
 */
public final class MathUtils {

    private MathUtils() {}

    /** Clamps {@code value} to [min, max]. */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Rounds a double to the given number of decimal places. */
    public static double roundToDecimalPlaces(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Places must be >= 0");
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Linear decay: how much to subtract given current progress toward max.
     * Result is clamped to [0, maxPenalty].
     */
    public static double linearDecay(double current, double max, double maxPenalty) {
        if (max <= 0) return 0;
        return clamp((current / max) * maxPenalty, 0, maxPenalty);
    }

    /**
     * Fibonacci via memoised recursion.
     *
     * @param n non-negative index
     * @return nth Fibonacci number
     */
    public static long fibonacci(int n) {
        return fibHelper(n, new long[Math.max(n + 2, 2)]);
    }

    private static long fibHelper(int n, long[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];
        memo[n] = fibHelper(n - 1, memo) + fibHelper(n - 2, memo);
        return memo[n];
    }

    /** Population standard deviation of the given values. */
    public static double standardDeviation(double[] values) {
        if (values == null || values.length == 0) return 0.0;
        double mean = 0;
        for (double v : values) mean += v;
        mean /= values.length;
        double variance = 0;
        for (double v : values) variance += Math.pow(v - mean, 2);
        variance /= values.length;
        return Math.sqrt(variance);
    }

    /** Iterative factorial. */
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative input");
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
}
