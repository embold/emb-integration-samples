package com.embold.sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    // ── clamp ─────────────────────────────────────────────────────────────────

    @Test
    void clamp_belowMin_returnsMin() {
        assertEquals(0.0, MathUtils.clamp(-5.0, 0.0, 10.0));
    }

    @Test
    void clamp_aboveMax_returnsMax() {
        assertEquals(10.0, MathUtils.clamp(15.0, 0.0, 10.0));
    }

    @Test
    void clamp_withinRange_returnsValue() {
        assertEquals(5.5, MathUtils.clamp(5.5, 0.0, 10.0));
    }

    @Test
    void clamp_atBoundaries_returnsBoundary() {
        assertEquals(0.0, MathUtils.clamp(0.0, 0.0, 10.0));
        assertEquals(10.0, MathUtils.clamp(10.0, 0.0, 10.0));
    }

    // ── roundToDecimalPlaces ──────────────────────────────────────────────────

    @Test
    void round_twoDecimalPlaces() {
        assertEquals(3.14, MathUtils.roundToDecimalPlaces(Math.PI, 2));
    }

    @Test
    void round_zeroDecimalPlaces() {
        assertEquals(3.0, MathUtils.roundToDecimalPlaces(3.499, 0));
    }

    @Test
    void round_negativePlaces_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> MathUtils.roundToDecimalPlaces(1.0, -1));
    }

    // ── linearDecay ──────────────────────────────────────────────────────────

    @Test
    void linearDecay_zeroMax_returnsZero() {
        assertEquals(0.0, MathUtils.linearDecay(5.0, 0.0, 10.0));
    }

    @Test
    void linearDecay_halfProgress_returnsHalfPenalty() {
        assertEquals(5.0, MathUtils.linearDecay(5.0, 10.0, 10.0), 1e-9);
    }

    @Test
    void linearDecay_resultClampedToMaxPenalty() {
        assertEquals(10.0, MathUtils.linearDecay(20.0, 10.0, 10.0));
    }

    // ── fibonacci ─────────────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "2,1", "6,8", "10,55"})
    void fibonacci_knownValues(int n, long expected) {
        assertEquals(expected, MathUtils.fibonacci(n));
    }

    // ── standardDeviation ────────────────────────────────────────────────────

    @Test
    void standardDeviation_emptyArray_returnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation(new double[0]));
    }

    @Test
    void standardDeviation_nullArray_returnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation(null));
    }

    @Test
    void standardDeviation_uniformValues_returnsZero() {
        assertEquals(0.0, MathUtils.standardDeviation(new double[]{4.0, 4.0, 4.0}), 1e-9);
    }

    @Test
    void standardDeviation_knownValues() {
        // population std-dev of [2, 4, 4, 4, 5, 5, 7, 9] == 2.0
        double[] values = {2, 4, 4, 4, 5, 5, 7, 9};
        assertEquals(2.0, MathUtils.standardDeviation(values), 1e-9);
    }
}
