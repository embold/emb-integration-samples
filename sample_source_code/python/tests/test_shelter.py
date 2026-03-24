"""Tests for Shelter."""
import pytest
from shelter import Shelter
from dog import Dog
from cat import Cat


@pytest.fixture
def shelter():
    return Shelter("Happy Paws", 5)

@pytest.fixture
def rex():
    return Dog("Rex", 3, "Labrador", 30.0)

@pytest.fixture
def whiskers():
    return Cat("Whiskers", 2, True, 4.5)


# ── name ──────────────────────────────────────────────────────────────────────

def test_name_returns_constructor_value(shelter):
    assert shelter.name == "Happy Paws"


# ── intake ────────────────────────────────────────────────────────────────────

def test_intake_under_capacity_returns_true(shelter, rex):
    assert shelter.intake(rex) is True

def test_intake_adds_animal(shelter, rex):
    shelter.intake(rex)
    assert len(shelter.animals) == 1

def test_intake_at_capacity_returns_false(rex, whiskers):
    tiny = Shelter("Tiny", 1)
    tiny.intake(rex)
    assert tiny.intake(whiskers) is False

def test_intake_at_capacity_does_not_add(rex, whiskers):
    tiny = Shelter("Tiny", 1)
    tiny.intake(rex)
    tiny.intake(whiskers)
    assert len(tiny.animals) == 1

def test_intake_multiple_animals(shelter, rex, whiskers):
    shelter.intake(rex)
    shelter.intake(whiskers)
    assert len(shelter.animals) == 2


# ── adopt ─────────────────────────────────────────────────────────────────────

def test_adopt_existing_returns_animal(shelter, rex):
    shelter.intake(rex)
    adopted = shelter.adopt("Rex")
    assert adopted is not None
    assert adopted.name == "Rex"

def test_adopt_removes_animal(shelter, rex):
    shelter.intake(rex)
    shelter.adopt("Rex")
    assert len(shelter.animals) == 0

def test_adopt_case_insensitive(shelter, rex):
    shelter.intake(rex)
    assert shelter.adopt("rex") is not None

def test_adopt_unknown_returns_none(shelter, rex):
    shelter.intake(rex)
    assert shelter.adopt("Unknown") is None

def test_adopt_unknown_does_not_remove_others(shelter, rex):
    shelter.intake(rex)
    shelter.adopt("Unknown")
    assert len(shelter.animals) == 1


# ── find_by_species ───────────────────────────────────────────────────────────

def test_find_by_species_returns_matching(shelter, rex, whiskers):
    shelter.intake(rex)
    shelter.intake(whiskers)
    cats = shelter.find_by_species("Felis catus")
    assert len(cats) == 1
    assert cats[0].name == "Whiskers"

def test_find_by_species_case_insensitive(shelter, rex):
    shelter.intake(rex)
    assert len(shelter.find_by_species("CANIS LUPUS FAMILIARIS")) == 1

def test_find_by_species_no_match_returns_empty(shelter, rex):
    shelter.intake(rex)
    assert shelter.find_by_species("Felis catus") == []


# ── available_capacity ────────────────────────────────────────────────────────

def test_available_capacity_empty_equals_max(shelter):
    assert shelter.available_capacity() == 5

def test_available_capacity_decreases_after_intake(shelter, rex):
    shelter.intake(rex)
    assert shelter.available_capacity() == 4

def test_available_capacity_increases_after_adopt(shelter, rex):
    shelter.intake(rex)
    shelter.adopt("Rex")
    assert shelter.available_capacity() == 5


# ── average_age ───────────────────────────────────────────────────────────────

def test_average_age_empty_shelter_returns_zero(shelter):
    assert shelter.average_age() == 0.0

def test_average_age_single_animal(shelter, rex):
    shelter.intake(rex)
    assert shelter.average_age() == pytest.approx(3.0)

def test_average_age_multiple_animals(shelter, rex, whiskers):
    shelter.intake(rex)       # age 3
    shelter.intake(whiskers)  # age 2
    assert shelter.average_age() == pytest.approx(2.5)


# ── animals property returns a copy ──────────────────────────────────────────

def test_animals_returns_copy(shelter, rex):
    shelter.intake(rex)
    copy = shelter.animals
    copy.clear()
    assert len(shelter.animals) == 1
