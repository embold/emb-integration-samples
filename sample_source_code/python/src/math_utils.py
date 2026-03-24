"""Math utility functions used across modules."""
from __future__ import annotations

import math


class MathUtils:
    """Pure static/class-method utilities. No instantiation needed."""

    @staticmethod
    def clamp(value: float, min_val: float, max_val: float) -> float:
        return max(min_val, min(max_val, value))

    @staticmethod
    def linear_decay(current: float, max_val: float, max_penalty: float) -> float:
        """Returns how much to subtract given current progress toward max_val."""
        if max_val <= 0:
            return 0.0
        return MathUtils.clamp((current / max_val) * max_penalty, 0.0, max_penalty)

    @staticmethod
    def fibonacci(n: int) -> int:
        """Memoised recursive Fibonacci."""
        memo: dict[int, int] = {}

        def _fib(k: int) -> int:
            if k <= 1:
                return k
            if k in memo:
                return memo[k]
            memo[k] = _fib(k - 1) + _fib(k - 2)
            return memo[k]

        return _fib(n)

    @staticmethod
    def standard_deviation(values: list[float]) -> float:
        """Population standard deviation."""
        if not values:
            return 0.0
        mean     = sum(values) / len(values)
        variance = sum((v - mean) ** 2 for v in values) / len(values)
        return math.sqrt(variance)

    @staticmethod
    def factorial(n: int) -> int:
        """Iterative factorial."""
        if n < 0:
            raise ValueError("Negative input to factorial")
        result = 1
        for i in range(2, n + 1):
            result *= i
        return result
