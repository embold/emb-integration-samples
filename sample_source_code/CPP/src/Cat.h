#pragma once
#include "Animal.h"

/**
 * Concrete Cat – inherits from Animal.
 * Adds indoor/outdoor lifespan estimation.
 */
class Cat : public Animal {
private:
    bool isIndoor_;

public:
    Cat(const std::string& name, int age, bool isIndoor, double weight);

    bool isIndoor() const;

    std::string sound()                const override;
    void        accept(IVisitor& visitor) override;

    /** Expected lifespan in years based on environment and weight. */
    int expectedLifespan() const;
};
