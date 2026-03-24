const Animal    = require('./Animal');
const MathUtils = require('./MathUtils');

/**
 * Concrete Cat with indoor/outdoor lifespan estimation.
 * @extends Animal
 */
class Cat extends Animal {
    /**
     * @param {string}  name
     * @param {number}  age
     * @param {boolean} isIndoor
     * @param {number}  weight
     */
    constructor(name, age, isIndoor, weight) {
        super(name, age, 'Felis catus', weight);
        this.isIndoor = isIndoor;
    }

    sound()         { return 'Meow'; }
    accept(visitor) { visitor.visitCat(this); }

    /**
     * Expected lifespan in years based on environment and weight.
     * @returns {number}
     */
    expectedLifespan() {
        const base       = this.isIndoor ? 15 : 10;
        const deviation  = Math.abs(this._weight - 4.5) / 4.5;
        const adjustment = Math.floor(MathUtils.linearDecay(deviation, 1.0, 5));
        return Math.max(5, base - adjustment);
    }
}

module.exports = Cat;
