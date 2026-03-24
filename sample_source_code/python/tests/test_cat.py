"""Tests for Cat."""
import pytest
from cat import Cat
from dog import Dog


class CountingVisitor:
    """Minimal visitor to verify double-dispatch."""
    def __init__(self):
        self.dog_visits = 0
        self.cat_visits = 0

    def visit_dog(self, dog):
        self.dog_visits += 1

    def visit_cat(self, cat):
        self.cat_visits += 1


class TestCatProperties:
    def setup_method(self):
        self.indoor_cat  = Cat("Whiskers", 3, True,   4.5)
        self.outdoor_cat = Cat("Shadow",   5, False,  7.0)

    def test_name(self):
        assert self.indoor_cat.name == "Whiskers"

    def test_age(self):
        assert self.indoor_cat.age == 3

    def test_species(self):
        assert self.indoor_cat.species == "Felis catus"

    def test_weight(self):
        assert self.indoor_cat.weight == 4.5

    def test_sound(self):
        assert self.indoor_cat.sound() == "Meow"

    def test_is_indoor_true(self):
        assert self.indoor_cat.is_indoor is True

    def test_is_indoor_false(self):
        assert self.outdoor_cat.is_indoor is False

    def test_describe_contains_name_and_sound(self):
        desc = self.indoor_cat.describe()
        assert "Whiskers" in desc
        assert "Meow" in desc


class TestCatExpectedLifespan:
    def test_indoor_greater_than_outdoor(self):
        indoor  = Cat("A", 2, True,  4.5)
        outdoor = Cat("B", 2, False, 4.5)
        assert indoor.expected_lifespan() > outdoor.expected_lifespan()

    def test_indoor_at_ideal_weight_no_penalty(self):
        # base=15, deviation=0 → adjustment=0
        cat = Cat("C", 3, True, 4.5)
        assert cat.expected_lifespan() == 15

    def test_outdoor_at_ideal_weight_base_ten(self):
        cat = Cat("D", 2, False, 4.5)
        assert cat.expected_lifespan() == 10

    def test_heavy_outdoor_cat_at_least_five(self):
        heavy = Cat("Fat", 10, False, 20.0)
        assert heavy.expected_lifespan() >= 5


class TestCatVisitor:
    def test_accept_increments_cat_count(self):
        v = CountingVisitor()
        Cat("Whiskers", 3, True, 4.5).accept(v)
        assert v.cat_visits == 1
        assert v.dog_visits == 0

    def test_accept_multiple_cats(self):
        v = CountingVisitor()
        for _ in range(2):
            Cat("X", 2, True, 4.5).accept(v)
        assert v.cat_visits == 2
