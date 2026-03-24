#pragma once
#include "Animal.h"
#include <string>

/**
 * Concrete Dog – inherits from Animal.
 * Adds breed-specific health scoring with outgoing calls to MathUtils.
 */
class Dog : public Animal {
private:
    std::string breed_;

public:
    Dog(const std::string& name, int age, const std::string& breed, double weight);

    std::string getBreed() const;

    std::string sound()                const override;
    void        accept(IVisitor& visitor) override;

    /** Health score 0-100 derived from age and weight vs breed ideal. */
    double computeHealthScore() const;

private:
    double estimateIdealWeight() const;
};
