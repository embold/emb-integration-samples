"""Abstract base class for all animals."""
from __future__ import annotations

from ianimal import IAnimal


class Animal(IAnimal):
    """Provides shared state and a default describe() implementation."""

    def __init__(self, name: str, age: int, species: str, weight: float) -> None:
        self._name    = name
        self._age     = age
        self._species = species
        self._weight  = weight

    @property
    def name(self) -> str:    return self._name

    @property
    def species(self) -> str: return self._species

    @property
    def age(self) -> int:     return self._age

    @property
    def weight(self) -> float: return self._weight

    def describe(self) -> str:
        return (
            f"{self._name} ({self._species}), age={self._age}, "
            f"weight={self._weight:.1f}kg, sound='{self.sound()}'"
        )

    def __repr__(self) -> str:
        return self.describe()
