#include "Dog.h"
#include "MathUtils.h"
#include <algorithm>
#include <cctype>
#include <cmath>

Dog::Dog(const std::string& name, int age, const std::string& breed, double weight)
    : Animal(name, age, "Canis lupus familiaris", weight), breed_(breed) {}

std::string Dog::getBreed() const { return breed_; }
std::string Dog::sound()    const { return "Woof"; }

void Dog::accept(IVisitor& visitor) { visitor.visit(*this); }

double Dog::computeHealthScore() const {
    double score = 100.0;

    if (age_ <= 2) {
        score -= MathUtils::linearDecay(age_, 2, 5);
    } else if (age_ <= 7) {
        score -= MathUtils::linearDecay(age_, 7, 10);
    } else {
        score -= MathUtils::linearDecay(age_, 15, 50);
    }

    double ideal     = estimateIdealWeight();
    double deviation = std::abs(weight_ - ideal) / ideal;
    score -= deviation * 20.0;

    return MathUtils::clamp(MathUtils::round2dp(score), 0.0, 100.0);
}

double Dog::estimateIdealWeight() const {
    std::string b = breed_;
    std::transform(b.begin(), b.end(), b.begin(), [](unsigned char c){ return std::tolower(c); });
    if (b == "chihuahua")       return 2.5;
    if (b == "labrador")        return 30.0;
    if (b == "german shepherd") return 35.0;
    return 15.0;
}
