#pragma once
#include "IAnimal.h"
#include <vector>
#include <memory>
#include <string>
#include <algorithm>
#include <numeric>

/**
 * Shelter owns a collection of IAnimal via shared_ptr.
 * Demonstrates composition (has-a relationship).
 * Incoming calls from main.cpp and AnimalVisitor.
 */
class Shelter {
private:
    std::string name_;
    int         capacity_;
    std::vector<std::shared_ptr<IAnimal>> animals_;

public:
    Shelter(const std::string& name, int capacity)
        : name_(name), capacity_(capacity) {}

    const std::string& getName() const { return name_; }

    /** Returns false if at capacity. */
    bool intake(std::shared_ptr<IAnimal> animal) {
        if (static_cast<int>(animals_.size()) >= capacity_) return false;
        animals_.push_back(std::move(animal));
        return true;
    }

    /** Removes and returns the named animal; returns nullptr if not found. */
    std::shared_ptr<IAnimal> adopt(const std::string& animalName) {
        auto it = std::find_if(animals_.begin(), animals_.end(),
            [&](const auto& a){ return a->getName() == animalName; });
        if (it == animals_.end()) return nullptr;
        auto found = *it;
        animals_.erase(it);
        return found;
    }

    const std::vector<std::shared_ptr<IAnimal>>& getAnimals() const { return animals_; }

    int availableCapacity() const {
        return capacity_ - static_cast<int>(animals_.size());
    }

    double averageAge() const {
        if (animals_.empty()) return 0.0;
        double sum = std::accumulate(animals_.begin(), animals_.end(), 0.0,
            [](double acc, const auto& a){ return acc + a->getAge(); });
        return sum / animals_.size();
    }
};
