#include <gtest/gtest.h>
#include "Dog.h"
#include "Cat.h"

// Minimal in-test visitor for verifying double-dispatch
struct CountingVisitor : public IVisitor {
    int dogVisits = 0;
    int catVisits = 0;
    void visit(Dog&) override { dogVisits++; }
    void visit(Cat&) override { catVisits++; }
};

class DogTest : public ::testing::Test {
protected:
    Dog labrador  {"Rex",   3, "Labrador",  30.0};
    Dog chihuahua {"Tiny",  1, "Chihuahua",  2.5};
    Dog defaultBreed{"Buddy", 8, "Poodle",  15.0};
};

// ── basic properties ──────────────────────────────────────────────────────────

TEST_F(DogTest, GetBreed_ReturnsCorrectBreed) {
    EXPECT_EQ("Labrador", labrador.getBreed());
}

TEST_F(DogTest, Sound_ReturnsWoof) {
    EXPECT_EQ("Woof", labrador.sound());
}

TEST_F(DogTest, GetName_ReturnsConstructorValue) {
    EXPECT_EQ("Rex", labrador.getName());
}

TEST_F(DogTest, GetAge_ReturnsConstructorValue) {
    EXPECT_EQ(3, labrador.getAge());
}

TEST_F(DogTest, GetSpecies_ReturnsCanisLupusFamiliaris) {
    EXPECT_EQ("Canis lupus familiaris", labrador.getSpecies());
}

TEST_F(DogTest, GetWeight_ReturnsConstructorValue) {
    EXPECT_DOUBLE_EQ(30.0, labrador.getWeight());
}

TEST_F(DogTest, Describe_ContainsNameAndSound) {
    const std::string desc = labrador.describe();
    EXPECT_NE(std::string::npos, desc.find("Rex"));
    EXPECT_NE(std::string::npos, desc.find("Woof"));
}

// ── computeHealthScore ────────────────────────────────────────────────────────

TEST_F(DogTest, HealthScore_IdealWeightYoungDog_NearHundred) {
    // chihuahua at ideal weight 2.5 kg, age 1 → very high score
    EXPECT_GE(chihuahua.computeHealthScore(), 90.0);
}

TEST_F(DogTest, HealthScore_IsWithinZeroToHundred) {
    EXPECT_GE(labrador.computeHealthScore(),    0.0);
    EXPECT_LE(labrador.computeHealthScore(),  100.0);
    EXPECT_GE(defaultBreed.computeHealthScore(),  0.0);
    EXPECT_LE(defaultBreed.computeHealthScore(), 100.0);
}

TEST_F(DogTest, HealthScore_OlderDog_LowerThanYounger) {
    Dog young{"A",  2, "Labrador", 30.0};
    Dog old  {"B", 12, "Labrador", 30.0};
    EXPECT_LT(old.computeHealthScore(), young.computeHealthScore());
}

TEST_F(DogTest, HealthScore_GermanShepherdIdealWeight_HighScore) {
    Dog gs{"Max", 3, "German Shepherd", 35.0};
    EXPECT_GE(gs.computeHealthScore(), 85.0);
}

// ── visitor double-dispatch ───────────────────────────────────────────────────

TEST_F(DogTest, Accept_IncrementsDogCountOnVisitor) {
    CountingVisitor v;
    labrador.accept(v);
    EXPECT_EQ(1, v.dogVisits);
    EXPECT_EQ(0, v.catVisits);
}

TEST_F(DogTest, Accept_MultipleDogs_CountsCorrectly) {
    CountingVisitor v;
    labrador.accept(v);
    chihuahua.accept(v);
    defaultBreed.accept(v);
    EXPECT_EQ(3, v.dogVisits);
    EXPECT_EQ(0, v.catVisits);
}
