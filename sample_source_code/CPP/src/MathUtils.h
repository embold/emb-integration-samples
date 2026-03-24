#pragma once
#include <cmath>
#include <vector>
#include <stdexcept>
#include <numeric>

/**
 * Math utilities called by Dog, Cat, AnimalVisitor, and main.
 * Header-only implementation for simplicity.
 */
namespace MathUtils {

/** Clamps value to [min, max]. */
inline double clamp(double value, double min, double max) {
    return std::max(min, std::min(max, value));
}

/** Rounds to 2 decimal places. */
inline double round2dp(double value) {
    return std::round(value * 100.0) / 100.0;
}

/**
 * Returns how much to subtract given current progress toward max.
 * Result is clamped to [0, maxPenalty].
 */
inline double linearDecay(double current, double max, double maxPenalty) {
    if (max <= 0.0) return 0.0;
    return clamp((current / max) * maxPenalty, 0.0, maxPenalty);
}

/** Iterative Fibonacci (avoids stack overflow for large n). */
inline long long fibonacci(int n) {
    if (n <= 1) return n;
    long long a = 0, b = 1;
    for (int i = 2; i <= n; ++i) { long long c = a + b; a = b; b = c; }
    return b;
}

/** Population standard deviation. */
inline double standardDeviation(const std::vector<double>& values) {
    if (values.empty()) return 0.0;
    double mean = std::accumulate(values.begin(), values.end(), 0.0) / values.size();
    double variance = 0.0;
    for (double v : values) variance += (v - mean) * (v - mean);
    variance /= values.size();
    return std::sqrt(variance);
}

/** Iterative factorial. */
inline long long factorial(int n) {
    if (n < 0) throw std::invalid_argument("Negative input to factorial");
    long long result = 1;
    for (int i = 2; i <= n; ++i) result *= i;
    return result;
}

} // namespace MathUtils
