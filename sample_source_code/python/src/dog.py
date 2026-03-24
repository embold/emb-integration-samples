"""Dog: concrete subclass of Animal with breed-specific health scoring."""
from __future__ import annotations

from animal import Animal
from math_utils import MathUtils


class Dog(Animal):
    """Dog with breed-specific ideal-weight lookup and health scoring."""

    _BREED_IDEAL_WEIGHT: dict[str, float] = {
        "chihuahua":       2.5,
        "labrador":        30.0,
        "german shepherd": 35.0,
    }

    def __init__(self, name: str, age: int, breed: str, weight: float) -> None:
        super().__init__(name, age, "Canis lupus familiaris", weight)
        self.breed = breed

    def sound(self) -> str:
        return "Woof"

    def accept(self, visitor) -> None:
        visitor.visit_dog(self)

    def compute_health_score(self) -> float:
        """
        Computes a health score (0–100) using age and weight heuristics.
        Demonstrates multiple branches and outgoing calls to MathUtils.
        """
        score = 100.0

        if self._age <= 2:
            score -= MathUtils.linear_decay(self._age, 2, 5)
        elif self._age <= 7:
            score -= MathUtils.linear_decay(self._age, 7, 10)
        else:
            score -= MathUtils.linear_decay(self._age, 15, 50)

        ideal     = self._BREED_IDEAL_WEIGHT.get(self.breed.lower(), 15.0)
        deviation = abs(self._weight - ideal) / ideal
        score    -= deviation * 20

        return MathUtils.clamp(round(score, 2), 0.0, 100.0)
