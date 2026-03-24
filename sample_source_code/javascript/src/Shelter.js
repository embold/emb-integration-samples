/**
 * Shelter: composes a bounded list of IAnimal instances.
 * Incoming calls from AnimalVisitor, Report, and main.
 */
class Shelter {
    /**
     * @param {string} name
     * @param {number} capacity
     */
    constructor(name, capacity) {
        this.name      = name;
        this.capacity  = capacity;
        /** @type {import('./IAnimal')[]} */
        this._animals  = [];
    }

    /**
     * Adds an animal; returns false if at capacity.
     * @param {import('./IAnimal')} animal
     * @returns {boolean}
     */
    intake(animal) {
        if (this._animals.length >= this.capacity) return false;
        this._animals.push(animal);
        return true;
    }

    /**
     * Removes and returns the named animal, or null.
     * @param {string} animalName
     * @returns {import('./IAnimal') | null}
     */
    adopt(animalName) {
        const idx = this._animals.findIndex(
            a => a.getName().toLowerCase() === animalName.toLowerCase()
        );
        if (idx === -1) return null;
        return this._animals.splice(idx, 1)[0];
    }

    /** @returns {import('./IAnimal')[]} */
    getAnimals()         { return [...this._animals]; }
    availableCapacity()  { return this.capacity - this._animals.length; }
    averageAge() {
        if (!this._animals.length) return 0;
        return this._animals.reduce((s, a) => s + a.getAge(), 0) / this._animals.length;
    }
}

module.exports = Shelter;
