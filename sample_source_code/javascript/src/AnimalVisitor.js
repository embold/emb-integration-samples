const IVisitor  = require('./IVisitor');
const MathUtils = require('./MathUtils');

/**
 * Concrete visitor: accumulates health stats while traversing animals.
 * @extends IVisitor
 */
class AnimalVisitor extends IVisitor {
    constructor() {
        super();
        this._dogCount    = 0;
        this._catCount    = 0;
        this._totalHealth = 0;
    }

    visitDog(dog) {
        this._dogCount++;
        this._totalHealth += dog.computeHealthScore();
    }

    visitCat(cat) {
        this._catCount++;
        const proxy = MathUtils.clamp(100 - Math.abs(cat.getWeight() - 4.5) * 10, 0, 100);
        this._totalHealth += proxy;
    }

    get dogCount()   { return this._dogCount; }
    get catCount()   { return this._catCount; }
    get totalCount() { return this._dogCount + this._catCount; }

    averageHealthScore() {
        return this.totalCount === 0
            ? 0
            : Math.round((this._totalHealth / this.totalCount) * 100) / 100;
    }
}

module.exports = AnimalVisitor;
