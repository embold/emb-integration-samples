"""AnimalVisitor: collects aggregate stats by traversing animals."""
from __future__ import annotations

from ivisitor import IVisitor
from math_utils import MathUtils


class AnimalVisitor(IVisitor):
    """
    Concrete visitor that accumulates per-species counts and health scores.
    Demonstrates double-dispatch via visit_dog / visit_cat.
    """

    def __init__(self) -> None:
        self._dog_count    = 0
        self._cat_count    = 0
        self._total_health = 0.0

    def visit_dog(self, dog) -> None:
        self._dog_count    += 1
        self._total_health += dog.compute_health_score()

    def visit_cat(self, cat) -> None:
        self._cat_count += 1
        proxy = MathUtils.clamp(100.0 - abs(cat.weight - 4.5) * 10, 0.0, 100.0)
        self._total_health += proxy

    @property
    def dog_count(self) -> int:   return self._dog_count

    @property
    def cat_count(self) -> int:   return self._cat_count

    @property
    def total_count(self) -> int: return self._dog_count + self._cat_count

    def average_health_score(self) -> float:
        if self.total_count == 0:
            return 0.0
        return round(self._total_health / self.total_count, 2)

    def reset(self) -> None:
        self._dog_count    = 0
        self._cat_count    = 0
        self._total_health = 0.0
