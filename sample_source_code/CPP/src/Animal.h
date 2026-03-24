#pragma once
#include "IAnimal.h"
#include <string>

/**
 * Abstract base class providing default field storage and describe() for all Animals.
 * Inherits from IAnimal; subclasses must implement sound() and accept().
 */
class Animal : public IAnimal {
protected:
    std::string name_;
    int         age_;
    std::string species_;
    double      weight_;

public:
    Animal(const std::string& name, int age, const std::string& species, double weight);
    virtual ~Animal() = default;

    std::string getName()    const override;
    std::string getSpecies() const override;
    int         getAge()     const override;
    double      getWeight()  const override;
    std::string describe()   const override;
};
