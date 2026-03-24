"""Report: generates a shelter summary using AnimalVisitor."""
from __future__ import annotations

from shelter import Shelter
from animal_visitor import AnimalVisitor
from math_utils import MathUtils


class Report:
    """Orchestrates Shelter + AnimalVisitor to produce a formatted text report."""

    def __init__(self, shelter: Shelter) -> None:
        self._shelter = shelter

    def generate(self) -> str:
        visitor = AnimalVisitor()
        for animal in self._shelter.animals:
            animal.accept(visitor)

        ages       = [float(a.age) for a in self._shelter.animals]
        age_stddev = MathUtils.standard_deviation(ages)

        lines = [
            f"=== Shelter Report: {self._shelter.name} ===",
            f"Total animals   : {visitor.total_count}",
            f"Dogs            : {visitor.dog_count}",
            f"Cats            : {visitor.cat_count}",
            f"Avg health score: {visitor.average_health_score():.2f}%",
            f"Avg age         : {self._shelter.average_age():.1f} years",
            f"Age std-dev     : {age_stddev:.2f}",
            f"Available cap   : {self._shelter.available_capacity()}",
            "--- Animal Details ---",
            *[f"  {a.describe()}" for a in self._shelter.animals],
        ]
        return "\n".join(lines)
