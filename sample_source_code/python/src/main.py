"""Entry point: wires together all classes."""
from __future__ import annotations

import sys
import os

sys.path.insert(0, os.path.dirname(__file__))

from dog import Dog
from cat import Cat
from shelter import Shelter
from report import Report
from math_utils import MathUtils


def main() -> None:
    shelter = Shelter("Happy Paws", capacity=20)

    shelter.intake(Dog("Buddy",    3, "Labrador",        28.5))
    shelter.intake(Dog("Max",      7, "German Shepherd", 33.0))
    shelter.intake(Dog("Tiny",     1, "Chihuahua",        2.1))
    shelter.intake(Cat("Whiskers", 5, True,  4.2))
    shelter.intake(Cat("Shadow",  10, False, 5.8))
    shelter.intake(Cat("Luna",     2, True,  3.9))

    print(Report(shelter).generate())

    print(f"\nfibonacci(10) = {MathUtils.fibonacci(10)}")
    print(f"factorial(6)  = {MathUtils.factorial(6)}")

    print("\nAdopting 'Max'...")
    adopted = shelter.adopt("Max")
    print(f"  Adopted: {adopted.name}" if adopted else "  Not found.")

    print("\nPost-adoption report:")
    print(Report(shelter).generate())


if __name__ == "__main__":
    main()
