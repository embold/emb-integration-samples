"""Interface definition for all animals (abstract base class)."""
from __future__ import annotations

from abc import ABC, abstractmethod


class IAnimal(ABC):
    """Abstract interface that all animal types must implement."""

    @property
    @abstractmethod
    def name(self) -> str: ...

    @property
    @abstractmethod
    def species(self) -> str: ...

    @property
    @abstractmethod
    def age(self) -> int: ...

    @property
    @abstractmethod
    def weight(self) -> float: ...

    @abstractmethod
    def sound(self) -> str: ...

    @abstractmethod
    def describe(self) -> str: ...

    @abstractmethod
    def accept(self, visitor: "IVisitor") -> None: ...
