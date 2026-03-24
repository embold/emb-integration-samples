#pragma once
#include <string>

// Forward declarations needed by the visitor interface
class Dog;
class Cat;

/**
 * Visitor interface for operations on animal types.
 * Enables double-dispatch without modifying the animal hierarchy.
 */
class IVisitor {
public:
    virtual ~IVisitor() = default;
    virtual void visit(Dog& dog) = 0;
    virtual void visit(Cat& cat) = 0;
};

/**
 * Pure abstract interface for all animals.
 * Replacing a Java-style interface in C++ via pure virtual methods.
 */
class IAnimal {
public:
    virtual ~IAnimal() = default;

    virtual std::string getName()    const = 0;
    virtual std::string getSpecies() const = 0;
    virtual int         getAge()     const = 0;
    virtual double      getWeight()  const = 0;
    virtual std::string sound()      const = 0;
    virtual std::string describe()   const = 0;
    virtual void        accept(IVisitor& visitor) = 0;
};
