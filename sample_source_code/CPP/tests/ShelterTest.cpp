#include <gtest/gtest.h>
#include "Shelter.h"
#include "Dog.h"
#include "Cat.h"

class ShelterTest : public ::testing::Test {
protected:
    Shelter shelter{"Happy Paws", 5};
    std::shared_ptr<Dog> rex      = std::make_shared<Dog>("Rex",      3, "Labrador", 30.0);
    std::shared_ptr<Cat> whiskers = std::make_shared<Cat>("Whiskers", 2, true,        4.5);
};

// ── getName ───────────────────────────────────────────────────────────────────

TEST_F(ShelterTest, GetName_ReturnsConstructorValue) {
    EXPECT_EQ("Happy Paws", shelter.getName());
}

// ── intake ────────────────────────────────────────────────────────────────────

TEST_F(ShelterTest, Intake_UnderCapacity_ReturnsTrue) {
    EXPECT_TRUE(shelter.intake(rex));
}

TEST_F(ShelterTest, Intake_AddsAnimalToList) {
    shelter.intake(rex);
    EXPECT_EQ(1u, shelter.getAnimals().size());
}

TEST_F(ShelterTest, Intake_AtCapacity_ReturnsFalse) {
    Shelter tiny{"Tiny", 1};
    tiny.intake(rex);
    EXPECT_FALSE(tiny.intake(whiskers));
}

TEST_F(ShelterTest, Intake_AtCapacity_DoesNotAddAnimal) {
    Shelter tiny{"Tiny", 1};
    tiny.intake(rex);
    tiny.intake(whiskers);
    EXPECT_EQ(1u, tiny.getAnimals().size());
}

TEST_F(ShelterTest, Intake_MultipleAnimals_AllAdded) {
    shelter.intake(rex);
    shelter.intake(whiskers);
    EXPECT_EQ(2u, shelter.getAnimals().size());
}

// ── adopt ─────────────────────────────────────────────────────────────────────

TEST_F(ShelterTest, Adopt_ExistingAnimal_ReturnsAnimal) {
    shelter.intake(rex);
    auto adopted = shelter.adopt("Rex");
    ASSERT_NE(nullptr, adopted);
    EXPECT_EQ("Rex", adopted->getName());
}

TEST_F(ShelterTest, Adopt_ExistingAnimal_RemovesFromShelter) {
    shelter.intake(rex);
    shelter.adopt("Rex");
    EXPECT_TRUE(shelter.getAnimals().empty());
}

TEST_F(ShelterTest, Adopt_UnknownName_ReturnsNullptr) {
    shelter.intake(rex);
    EXPECT_EQ(nullptr, shelter.adopt("Unknown"));
}

TEST_F(ShelterTest, Adopt_UnknownName_DoesNotRemoveOtherAnimals) {
    shelter.intake(rex);
    shelter.adopt("Unknown");
    EXPECT_EQ(1u, shelter.getAnimals().size());
}

// ── availableCapacity ─────────────────────────────────────────────────────────

TEST_F(ShelterTest, AvailableCapacity_EmptyShelter_EqualsCapacity) {
    EXPECT_EQ(5, shelter.availableCapacity());
}

TEST_F(ShelterTest, AvailableCapacity_AfterOneIntake_Decreases) {
    shelter.intake(rex);
    EXPECT_EQ(4, shelter.availableCapacity());
}

TEST_F(ShelterTest, AvailableCapacity_AfterAdopt_Increases) {
    shelter.intake(rex);
    shelter.adopt("Rex");
    EXPECT_EQ(5, shelter.availableCapacity());
}

// ── averageAge ────────────────────────────────────────────────────────────────

TEST_F(ShelterTest, AverageAge_EmptyShelter_ReturnsZero) {
    EXPECT_DOUBLE_EQ(0.0, shelter.averageAge());
}

TEST_F(ShelterTest, AverageAge_SingleAnimal_ReturnsItsAge) {
    shelter.intake(rex);   // age 3
    EXPECT_DOUBLE_EQ(3.0, shelter.averageAge());
}

TEST_F(ShelterTest, AverageAge_MultipleAnimals_IsCorrect) {
    shelter.intake(rex);       // age 3
    shelter.intake(whiskers);  // age 2
    EXPECT_NEAR(2.5, shelter.averageAge(), 1e-9);
}
