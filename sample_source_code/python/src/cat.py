"""Cat: concrete subclass of Animal with lifespan estimation."""
from __future__ import annotations

from animal import Animal
from math_utils import MathUtils


class Cat(Animal):
    """Cat with indoor/outdoor lifespan calculation."""

    def __init__(self, name: str, age: int, is_indoor: bool, weight: float) -> None:
        super().__init__(name, age, "Felis catus", weight)
        self.is_indoor = is_indoor

    def sound(self) -> str:
        return "Meow"

    def accept(self, visitor) -> None:
        visitor.visit_cat(self)

    def expected_lifespan(self) -> int:
        """Returns expected lifespan adjusted for environment and weight."""
        base       = 15 if self.is_indoor else 10
        deviation  = abs(self._weight - 4.5) / 4.5
        adjustment = int(MathUtils.linear_decay(deviation, 1.0, 5))
        return max(5, base - adjustment)
