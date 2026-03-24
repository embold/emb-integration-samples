#include "Cat.h"
#include "MathUtils.h"
#include <cmath>
#include <algorithm>

Cat::Cat(const std::string& name, int age, bool isIndoor, double weight)
    : Animal(name, age, "Felis catus", weight), isIndoor_(isIndoor) {}

bool        Cat::isIndoor() const { return isIndoor_; }
std::string Cat::sound()    const { return "Meow"; }

void Cat::accept(IVisitor& visitor) { visitor.visit(*this); }

int Cat::expectedLifespan() const {
    int    base      = isIndoor_ ? 15 : 10;
    double deviation = std::abs(weight_ - 4.5) / 4.5;
    int    adjustment = static_cast<int>(MathUtils::linearDecay(deviation, 1.0, 5.0));
    return std::max(5, base - adjustment);
}
