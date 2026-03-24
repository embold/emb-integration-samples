const Animal    = require('./Animal');
const MathUtils = require('./MathUtils');

/** @type {Record<string, number>} */
const BREED_IDEAL_WEIGHT = {
    'chihuahua':       2.5,
    'labrador':        30.0,
    'german shepherd': 35.0,
};

/**
 * Concrete Dog with breed-specific health scoring.
 * @extends Animal
 */
class Dog extends Animal {
    /**
     * @param {string} name
     * @param {number} age
     * @param {string} breed
     * @param {number} weight
     */
    constructor(name, age, breed, weight) {
        super(name, age, 'Canis lupus familiaris', weight);
        this.breed = breed;
    }

    sound()          { return 'Woof'; }
    accept(visitor)  { visitor.visitDog(this); }

    /**
     * Computes a health score (0-100) based on age and weight vs breed ideal.
     * @returns {number}
     */
    computeHealthScore() {
        let score = 100;

        if (this._age <= 2) {
            score -= MathUtils.linearDecay(this._age, 2, 5);
        } else if (this._age <= 7) {
            score -= MathUtils.linearDecay(this._age, 7, 10);
        } else {
            score -= MathUtils.linearDecay(this._age, 15, 50);
        }

        const ideal     = BREED_IDEAL_WEIGHT[this.breed.toLowerCase()] ?? 15;
        const deviation = Math.abs(this._weight - ideal) / ideal;
        score -= deviation * 20;

        return MathUtils.clamp(Math.round(score * 100) / 100, 0, 100);
    }
}

module.exports = Dog;
