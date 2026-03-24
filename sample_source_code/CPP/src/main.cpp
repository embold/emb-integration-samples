#include <iostream>
#include <memory>
#include <vector>
#include <cmath>
#include "Dog.h"
#include "Cat.h"
#include "Shelter.h"
#include "MathUtils.h"

/**
 * Inline AnimalVisitor defined in main for simplicity.
 * Demonstrates double-dispatch: each animal type routes to a separate visit() overload.
 */
class AnimalVisitor : public IVisitor {
    int    dogs_        = 0;
    int    cats_        = 0;
    double totalHealth_ = 0.0;

public:
    void visit(Dog& dog) override {
        ++dogs_;
        totalHealth_ += dog.computeHealthScore();
    }
    void visit(Cat& cat) override {
        ++cats_;
        double proxy = MathUtils::clamp(100.0 - std::abs(cat.getWeight() - 4.5) * 10.0, 0.0, 100.0);
        totalHealth_ += proxy;
    }
    int    dogCount()    const { return dogs_; }
    int    catCount()    const { return cats_; }
    int    totalCount()  const { return dogs_ + cats_; }
    double avgHealth()   const {
        return totalCount() > 0 ? MathUtils::round2dp(totalHealth_ / totalCount()) : 0.0;
    }
};

int main() {
    auto shelter = std::make_shared<Shelter>("Happy Paws", 20);

    shelter->intake(std::make_shared<Dog>("Buddy",    3, "Labrador",        28.5));
    shelter->intake(std::make_shared<Dog>("Max",      7, "German Shepherd", 33.0));
    shelter->intake(std::make_shared<Dog>("Tiny",     1, "Chihuahua",        2.1));
    shelter->intake(std::make_shared<Cat>("Whiskers", 5, true,   4.2));
    shelter->intake(std::make_shared<Cat>("Shadow",  10, false,  5.8));
    shelter->intake(std::make_shared<Cat>("Luna",     2, true,   3.9));

    AnimalVisitor visitor;
    std::vector<double> ages;
    for (auto& a : shelter->getAnimals()) {
        a->accept(visitor);
        ages.push_back(static_cast<double>(a->getAge()));
    }

    std::cout << "=== Shelter: " << shelter->getName() << " ===" << "\n";
    std::cout << "Dogs            : " << visitor.dogCount()  << "\n";
    std::cout << "Cats            : " << visitor.catCount()  << "\n";
    std::cout << "Avg health score: " << visitor.avgHealth() << "%\n";
    std::cout << "Avg age         : " << shelter->averageAge() << " years\n";
    std::cout << "Age std-dev     : " << MathUtils::standardDeviation(ages) << "\n";
    std::cout << "fibonacci(10)   : " << MathUtils::fibonacci(10) << "\n";
    std::cout << "factorial(6)    : " << MathUtils::factorial(6)  << "\n";
    std::cout << "--- Animal Details ---\n";
    for (auto& a : shelter->getAnimals()) {
        std::cout << "  " << a->describe() << "\n";
    }

    std::cout << "\nAdopting 'Max'...\n";
    auto adopted = shelter->adopt("Max");
    std::cout << (adopted ? "  Adopted: " + adopted->getName() : "  Not found.") << "\n";

    return 0;
}
