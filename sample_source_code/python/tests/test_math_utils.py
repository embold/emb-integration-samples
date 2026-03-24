"""Tests for MathUtils."""
import math
import pytest
from math_utils import MathUtils


# ── clamp ─────────────────────────────────────────────────────────────────────

class TestClamp:
    def test_below_min_returns_min(self):
        assert MathUtils.clamp(-5.0, 0.0, 10.0) == 0.0

    def test_above_max_returns_max(self):
        assert MathUtils.clamp(15.0, 0.0, 10.0) == 10.0

    def test_within_range_returns_value(self):
        assert MathUtils.clamp(5.5, 0.0, 10.0) == 5.5

    def test_at_min_boundary(self):
        assert MathUtils.clamp(0.0, 0.0, 10.0) == 0.0

    def test_at_max_boundary(self):
        assert MathUtils.clamp(10.0, 0.0, 10.0) == 10.0


# ── linear_decay ──────────────────────────────────────────────────────────────

class TestLinearDecay:
    def test_zero_max_returns_zero(self):
        assert MathUtils.linear_decay(5.0, 0.0, 10.0) == 0.0

    def test_half_progress_returns_half_penalty(self):
        assert MathUtils.linear_decay(5.0, 10.0, 10.0) == pytest.approx(5.0)

    def test_over_max_clamps_to_max_penalty(self):
        assert MathUtils.linear_decay(20.0, 10.0, 10.0) == 10.0

    def test_zero_progress_returns_zero(self):
        assert MathUtils.linear_decay(0.0, 10.0, 10.0) == 0.0


# ── fibonacci ─────────────────────────────────────────────────────────────────

class TestFibonacci:
    @pytest.mark.parametrize("n, expected", [
        (0, 0), (1, 1), (2, 1), (6, 8), (10, 55),
    ])
    def test_known_values(self, n, expected):
        assert MathUtils.fibonacci(n) == expected


# ── standard_deviation ───────────────────────────────────────────────────────

class TestStandardDeviation:
    def test_empty_list_returns_zero(self):
        assert MathUtils.standard_deviation([]) == 0.0

    def test_uniform_values_returns_zero(self):
        assert MathUtils.standard_deviation([4.0, 4.0, 4.0]) == pytest.approx(0.0)

    def test_known_values_returns_two(self):
        # population std-dev of [2,4,4,4,5,5,7,9] == 2.0
        assert MathUtils.standard_deviation([2, 4, 4, 4, 5, 5, 7, 9]) == pytest.approx(2.0)

    def test_single_element_returns_zero(self):
        assert MathUtils.standard_deviation([42.0]) == 0.0


# ── factorial ─────────────────────────────────────────────────────────────────

class TestFactorial:
    def test_zero_returns_one(self):
        assert MathUtils.factorial(0) == 1

    def test_one_returns_one(self):
        assert MathUtils.factorial(1) == 1

    def test_five_returns_120(self):
        assert MathUtils.factorial(5) == 120

    def test_negative_raises_value_error(self):
        with pytest.raises(ValueError):
            MathUtils.factorial(-1)
