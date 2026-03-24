/**
 * @interface IAnimal
 * Base interface for all animals — enforced via thrown errors in JS.
 */
class IAnimal {
    /** @returns {string} */ getName()    { throw new Error('Not implemented'); }
    /** @returns {string} */ getSpecies() { throw new Error('Not implemented'); }
    /** @returns {number} */ getAge()     { throw new Error('Not implemented'); }
    /** @returns {number} */ getWeight()  { throw new Error('Not implemented'); }
    /** @returns {string} */ sound()      { throw new Error('Not implemented'); }
    /** @returns {string} */ describe()   { throw new Error('Not implemented'); }
    /** @param {IVisitor} visitor */ accept(visitor) { throw new Error('Not implemented'); }
}

module.exports = IAnimal;
