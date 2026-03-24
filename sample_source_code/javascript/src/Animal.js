const IAnimal = require('./IAnimal');

/**
 * Abstract base class providing shared state and describe().
 * @extends IAnimal
 */
class Animal extends IAnimal {
    /**
     * @param {string} name
     * @param {number} age
     * @param {string} species
     * @param {number} weight
     */
    constructor(name, age, species, weight) {
        super();
        this._name    = name;
        this._age     = age;
        this._species = species;
        this._weight  = weight;
    }

    getName()    { return this._name; }
    getSpecies() { return this._species; }
    getAge()     { return this._age; }
    getWeight()  { return this._weight; }

    describe() {
        return `${this._name} (${this._species}), age=${this._age}, ` +
               `weight=${this._weight.toFixed(1)}kg, sound='${this.sound()}'`;
    }

    toString() { return this.describe(); }
}

module.exports = Animal;
