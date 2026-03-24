"""Shelter: composition of IAnimal instances."""
from __future__ import annotations

from typing import Optional
from ianimal import IAnimal


class Shelter:
    """Manages a bounded collection of IAnimal instances (composition pattern)."""

    def __init__(self, name: str, capacity: int) -> None:
        self.name     = name
        self.capacity = capacity
        self._animals: list[IAnimal] = []

    def intake(self, animal: IAnimal) -> bool:
        """Add an animal; returns False if at capacity."""
        if len(self._animals) >= self.capacity:
            return False
        self._animals.append(animal)
        return True

    def adopt(self, animal_name: str) -> Optional[IAnimal]:
        """Remove and return the named animal, or None if not found."""
        for i, a in enumerate(self._animals):
            if a.name.lower() == animal_name.lower():
                return self._animals.pop(i)
        return None

    @property
    def animals(self) -> list[IAnimal]:
        return list(self._animals)

    def find_by_species(self, species: str) -> list[IAnimal]:
        return [a for a in self._animals if a.species.lower() == species.lower()]

    def available_capacity(self) -> int:
        return self.capacity - len(self._animals)

    def average_age(self) -> float:
        if not self._animals:
            return 0.0
        return sum(a.age for a in self._animals) / len(self._animals)
