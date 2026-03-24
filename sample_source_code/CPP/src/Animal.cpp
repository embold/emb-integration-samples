#include "Animal.h"
#include <sstream>
#include <iomanip>

Animal::Animal(const std::string& name, int age, const std::string& species, double weight)
    : name_(name), age_(age), species_(species), weight_(weight) {}

std::string Animal::getName()    const { return name_; }
std::string Animal::getSpecies() const { return species_; }
int         Animal::getAge()     const { return age_; }
double      Animal::getWeight()  const { return weight_; }

std::string Animal::describe() const {
    std::ostringstream ss;
    ss << name_ << " (" << species_ << "), age=" << age_
       << ", weight=" << std::fixed << std::setprecision(1) << weight_ << "kg"
       << ", sound='" << sound() << "'";
    return ss.str();
}
