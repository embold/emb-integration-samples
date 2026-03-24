#include <gtest/gtest.h>
#include "Cat.h"
#include "Dog.h"

// Minimal in-test visitor for verifying double-dispatch
struct CountingVisitor : public IVisitor {
    int dogVisits = 0;
    int catVisits = 0;
    void visit(Dog&) override { dogVisits++; }
    void visit(Cat&) override { catVisits++; }
};

class CatTest : public ::testing::Test {
protected:
    Cat indoorCat {"Whiskers", 3, true,   4.5};  // ideal weight
    Cat outdoorCat{"Shadow",   5, false,  7.0};  // above ideal
};

// ── basic properties ──────────────────────────────────────────────────────────

TEST_F(CatTest, Sound_ReturnsMeow) {
    EXPECT_EQ("Meow", indoorCat.sound());
}

TEST_F(CatTest, IsIndoor_ReflectsConstructorFlag) {
    EXPECT_TRUE(indoorCat.isIndoor());
    EXPECT_FALSE(outdoorCat.isIndoor());
}

TEST_F(CatTest, GetName_ReturnsConstructorValue) {
    EXPECT_EQ("Whiskers", indoorCat.getName());
}

TEST_F(CatTest, GetAge_ReturnsConstructorValue) {
    EXPECT_EQ(3, indoorCat.getAge());
}

TEST_F(CatTest, GetSpecies_ReturnsFeliscatus) {
    EXPECT_EQ("Felis catus", indoorCat.getSpecies());
}

TEST_F(CatTest, GetWeight_ReturnsConstructorValue) {
    EXPECT_DOUBLE_EQ(4.5, indoorCat.getWeight());
}

TEST_F(CatTest, Describe_ContainsNameAndSound) {
    const std::string desc = indoorCat.describe();
    EXPECT_NE(std::string::npos, desc.find("Whiskers"));
    EXPECT_NE(std::string::npos, desc.find("Meow"));
}

// ── expectedLifespan ──────────────────────────────────────────────────────────

TEST_F(CatTest, ExpectedLifespan_Indoor_GreaterThanOutdoor) {
    Cat indoor {"A", 2, true,  4.5};
    Cat outdoor{"B", 2, false, 4.5};
    EXPECT_GT(indoor.expectedLifespan(), outdoor.expectedLifespan());
}

TEST_F(CatTest, ExpectedLifespan_IndoorAtIdealWeight_NoPenalty) {
    // base=15, deviation=0 → adjustment=0, lifespan=15
    EXPECT_EQ(15, indoorCat.expectedLifespan());
}

TEST_F(CatTest, ExpectedLifespan_HeavyOutdoorCat_AtLeastFive) {
    Cat heavyCat{"Fat", 10, false, 20.0};
    EXPECT_GE(heavyCat.expectedLifespan(), 5);
}

TEST_F(CatTest, ExpectedLifespan_OutdoorIdealWeight_BaseTen) {
    Cat outdoor{"C", 2, false, 4.5};
    // base=10, deviation=0 → adjustment=0, lifespan=10
    EXPECT_EQ(10, outdoor.expectedLifespan());
}

// ── visitor double-dispatch ───────────────────────────────────────────────────

TEST_F(CatTest, Accept_IncrementsCatCountOnVisitor) {
    CountingVisitor v;
    indoorCat.accept(v);
    EXPECT_EQ(1, v.catVisits);
    EXPECT_EQ(0, v.dogVisits);
}

TEST_F(CatTest, Accept_MultipleCats_CountsCorrectly) {
    CountingVisitor v;
    indoorCat.accept(v);
    outdoorCat.accept(v);
    EXPECT_EQ(2, v.catVisits);
    EXPECT_EQ(0, v.dogVisits);
}
