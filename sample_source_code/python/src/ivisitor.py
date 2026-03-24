"""Visitor interface for operations on animal types."""
from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from dog import Dog
    from cat import Cat


class IVisitor(ABC):
    """Visitor pattern interface enabling double-dispatch."""

    @abstractmethod
    def visit_dog(self, dog: "Dog") -> None: ...

    @abstractmethod
    def visit_cat(self, cat: "Cat") -> None: ...
