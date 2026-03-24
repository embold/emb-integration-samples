#include <gtest/gtest.h>
#include "MathUtils.h"

// ── clamp ─────────────────────────────────────────────────────────────────────

TEST(MathUtilsTest, Clamp_BelowMin_ReturnsMin) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::clamp(-5.0, 0.0, 10.0));
}

TEST(MathUtilsTest, Clamp_AboveMax_ReturnsMax) {
    EXPECT_DOUBLE_EQ(10.0, MathUtils::clamp(15.0, 0.0, 10.0));
}

TEST(MathUtilsTest, Clamp_WithinRange_ReturnsValue) {
    EXPECT_DOUBLE_EQ(5.5, MathUtils::clamp(5.5, 0.0, 10.0));
}

TEST(MathUtilsTest, Clamp_AtMin_ReturnsMin) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::clamp(0.0, 0.0, 10.0));
}

TEST(MathUtilsTest, Clamp_AtMax_ReturnsMax) {
    EXPECT_DOUBLE_EQ(10.0, MathUtils::clamp(10.0, 0.0, 10.0));
}

// ── round2dp ──────────────────────────────────────────────────────────────────

TEST(MathUtilsTest, Round2dp_Pi_ReturnsTwoDecimalPlaces) {
    EXPECT_DOUBLE_EQ(3.14, MathUtils::round2dp(3.14159));
}

TEST(MathUtilsTest, Round2dp_AlreadyRounded_Unchanged) {
    EXPECT_DOUBLE_EQ(2.50, MathUtils::round2dp(2.50));
}

TEST(MathUtilsTest, Round2dp_RoundsUp) {
    EXPECT_DOUBLE_EQ(1.46, MathUtils::round2dp(1.456));
}

// ── linearDecay ───────────────────────────────────────────────────────────────

TEST(MathUtilsTest, LinearDecay_ZeroMax_ReturnsZero) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::linearDecay(5.0, 0.0, 10.0));
}

TEST(MathUtilsTest, LinearDecay_HalfProgress_ReturnsHalfPenalty) {
    EXPECT_NEAR(5.0, MathUtils::linearDecay(5.0, 10.0, 10.0), 1e-9);
}

TEST(MathUtilsTest, LinearDecay_OverMax_ClampsToMaxPenalty) {
    EXPECT_DOUBLE_EQ(10.0, MathUtils::linearDecay(20.0, 10.0, 10.0));
}

TEST(MathUtilsTest, LinearDecay_ZeroProgress_ReturnsZero) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::linearDecay(0.0, 10.0, 10.0));
}

// ── fibonacci ─────────────────────────────────────────────────────────────────

TEST(MathUtilsTest, Fibonacci_BaseCase_Zero) {
    EXPECT_EQ(0LL, MathUtils::fibonacci(0));
}

TEST(MathUtilsTest, Fibonacci_BaseCase_One) {
    EXPECT_EQ(1LL, MathUtils::fibonacci(1));
}

TEST(MathUtilsTest, Fibonacci_N2) {
    EXPECT_EQ(1LL, MathUtils::fibonacci(2));
}

TEST(MathUtilsTest, Fibonacci_N6_Returns8) {
    EXPECT_EQ(8LL, MathUtils::fibonacci(6));
}

TEST(MathUtilsTest, Fibonacci_N10_Returns55) {
    EXPECT_EQ(55LL, MathUtils::fibonacci(10));
}

// ── standardDeviation ────────────────────────────────────────────────────────

TEST(MathUtilsTest, StdDev_EmptyVector_ReturnsZero) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::standardDeviation({}));
}

TEST(MathUtilsTest, StdDev_UniformValues_ReturnsZero) {
    EXPECT_NEAR(0.0, MathUtils::standardDeviation({4.0, 4.0, 4.0}), 1e-9);
}

TEST(MathUtilsTest, StdDev_KnownValues_ReturnsTwo) {
    // population std-dev of {2,4,4,4,5,5,7,9} == 2.0
    EXPECT_NEAR(2.0, MathUtils::standardDeviation({2, 4, 4, 4, 5, 5, 7, 9}), 1e-9);
}

TEST(MathUtilsTest, StdDev_SingleElement_ReturnsZero) {
    EXPECT_DOUBLE_EQ(0.0, MathUtils::standardDeviation({42.0}));
}

// ── factorial ────────────────────────────────────────────────────────────────

TEST(MathUtilsTest, Factorial_Zero_ReturnsOne) {
    EXPECT_EQ(1LL, MathUtils::factorial(0));
}

TEST(MathUtilsTest, Factorial_One_ReturnsOne) {
    EXPECT_EQ(1LL, MathUtils::factorial(1));
}

TEST(MathUtilsTest, Factorial_Five_Returns120) {
    EXPECT_EQ(120LL, MathUtils::factorial(5));
}

TEST(MathUtilsTest, Factorial_Negative_Throws) {
    EXPECT_THROW(MathUtils::factorial(-1), std::invalid_argument);
}
