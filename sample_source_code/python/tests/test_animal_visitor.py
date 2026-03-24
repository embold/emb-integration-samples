"""Tests for AnimalVisitor."""
import pytest
from animal_visitor import AnimalVisitor
from dog import Dog
from cat import Cat


@pytest.fixture
def visitor():
    return AnimalVisitor()

@pytest.fixture
def labrador():
    return Dog("Rex", 3, "Labrador", 30.0)

@pytest.fixture
def indoor_cat():
    return Cat("Whiskers", 2, True, 4.5)


# ── initial state ─────────────────────────────────────────────────────────────

def test_initial_dog_count_is_zero(visitor):
    assert visitor.dog_count == 0

def test_initial_cat_count_is_zero(visitor):
    assert visitor.cat_count == 0

def test_initial_total_count_is_zero(visitor):
    assert visitor.total_count == 0

def test_initial_average_health_score_is_zero(visitor):
    assert visitor.average_health_score() == 0.0


# ── visit_dog ─────────────────────────────────────────────────────────────────

def test_visit_dog_increments_dog_count(visitor, labrador):
    visitor.visit_dog(labrador)
    assert visitor.dog_count == 1
    assert visitor.cat_count == 0

def test_visit_dog_updates_total_count(visitor, labrador):
    visitor.visit_dog(labrador)
    assert visitor.total_count == 1

def test_visit_dog_health_score_is_positive(visitor, labrador):
    visitor.visit_dog(labrador)
    assert visitor.average_health_score() > 0.0


# ── visit_cat ─────────────────────────────────────────────────────────────────

def test_visit_cat_increments_cat_count(visitor, indoor_cat):
    visitor.visit_cat(indoor_cat)
    assert visitor.cat_count == 1
    assert visitor.dog_count == 0

def test_visit_cat_ideal_weight_health_score_100(visitor, indoor_cat):
    # weight=4.5 == ideal → proxy = 100 - |4.5-4.5|*10 = 100
    visitor.visit_cat(indoor_cat)
    assert visitor.average_health_score() == pytest.approx(100.0)


# ── mixed visits ─────────────────────────────────────────────────────────────

def test_mixed_visits_counts(visitor, labrador, indoor_cat):
    visitor.visit_dog(labrador)
    visitor.visit_cat(indoor_cat)
    visitor.visit_dog(Dog("Max", 5, "Poodle", 15.0))
    assert visitor.dog_count == 2
    assert visitor.cat_count == 1
    assert visitor.total_count == 3

def test_average_health_score_after_mixed_within_range(visitor, labrador, indoor_cat):
    visitor.visit_dog(labrador)
    visitor.visit_cat(indoor_cat)
    avg = visitor.average_health_score()
    assert 0.0 <= avg <= 100.0


# ── reset ─────────────────────────────────────────────────────────────────────

def test_reset_clears_all_counters(visitor, labrador, indoor_cat):
    visitor.visit_dog(labrador)
    visitor.visit_cat(indoor_cat)
    visitor.reset()
    assert visitor.dog_count == 0
    assert visitor.cat_count == 0
    assert visitor.total_count == 0
    assert visitor.average_health_score() == 0.0


# ── double-dispatch via accept ────────────────────────────────────────────────

def test_dog_accept_uses_visit_dog(visitor, labrador):
    labrador.accept(visitor)
    assert visitor.dog_count == 1

def test_cat_accept_uses_visit_cat(visitor, indoor_cat):
    indoor_cat.accept(visitor)
    assert visitor.cat_count == 1
