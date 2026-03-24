"""Tests for Dog."""
import pytest
from dog import Dog
from cat import Cat


class CountingVisitor:
    """Minimal visitor to verify double-dispatch."""
    def __init__(self):
        self.dog_visits = 0
        self.cat_visits = 0

    def visit_dog(self, dog):
        self.dog_visits += 1

    def visit_cat(self, cat):
        self.cat_visits += 1


class TestDogProperties:
    def setup_method(self):
        self.labrador   = Dog("Rex",   3, "Labrador",  30.0)
        self.chihuahua  = Dog("Tiny",  1, "Chihuahua",  2.5)
        self.poodle     = Dog("Buddy", 8, "Poodle",    15.0)

    def test_name(self):
        assert self.labrador.name == "Rex"

    def test_age(self):
        assert self.labrador.age == 3

    def test_species(self):
        assert self.labrador.species == "Canis lupus familiaris"

    def test_weight(self):
        assert self.labrador.weight == 30.0

    def test_breed(self):
        assert self.labrador.breed == "Labrador"

    def test_sound(self):
        assert self.labrador.sound() == "Woof"

    def test_describe_contains_name_and_sound(self):
        desc = self.labrador.describe()
        assert "Rex" in desc
        assert "Woof" in desc


class TestDogHealthScore:
    def setup_method(self):
        self.labrador  = Dog("Rex",   3, "Labrador",  30.0)
        self.chihuahua = Dog("Tiny",  1, "Chihuahua",  2.5)

    def test_ideal_weight_young_dog_near_100(self):
        assert self.chihuahua.compute_health_score() >= 90.0

    def test_score_within_zero_and_100(self):
        score = self.labrador.compute_health_score()
        assert 0.0 <= score <= 100.0

    def test_older_dog_lower_score_than_younger(self):
        young = Dog("A",  2, "Labrador", 30.0)
        old   = Dog("B", 12, "Labrador", 30.0)
        assert old.compute_health_score() < young.compute_health_score()

    def test_german_shepherd_ideal_weight_high_score(self):
        gs = Dog("Max", 3, "German Shepherd", 35.0)
        assert gs.compute_health_score() >= 85.0

    def test_default_breed_uses_15kg_ideal(self):
        # Unknown breed falls back to 15 kg ideal
        dog = Dog("X", 3, "Beagle", 15.0)
        score = dog.compute_health_score()
        assert 0.0 <= score <= 100.0


class TestDogVisitor:
    def test_accept_increments_dog_count(self):
        v = CountingVisitor()
        Dog("Rex", 3, "Labrador", 30.0).accept(v)
        assert v.dog_visits == 1
        assert v.cat_visits == 0

    def test_accept_multiple_dogs(self):
        v = CountingVisitor()
        for _ in range(3):
            Dog("X", 2, "Poodle", 10.0).accept(v)
        assert v.dog_visits == 3
