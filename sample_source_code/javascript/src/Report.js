const AnimalVisitor = require('./AnimalVisitor');
const MathUtils     = require('./MathUtils');

/**
 * Report: generates a formatted shelter summary using the visitor pattern.
 * Orchestrates calls to Shelter, AnimalVisitor, and MathUtils.
 */
class Report {
    /** @param {import('./Shelter')} shelter */
    constructor(shelter) {
        this._shelter = shelter;
    }

    generate() {
        const visitor = new AnimalVisitor();
        const animals = this._shelter.getAnimals();
        animals.forEach(a => a.accept(visitor));

        const ages      = animals.map(a => a.getAge());
        const ageStdDev = MathUtils.standardDeviation(ages);

        const lines = [
            `=== Shelter Report: ${this._shelter.name} ===`,
            `Total animals   : ${visitor.totalCount}`,
            `Dogs            : ${visitor.dogCount}`,
            `Cats            : ${visitor.catCount}`,
            `Avg health score: ${visitor.averageHealthScore().toFixed(2)}%`,
            `Avg age         : ${this._shelter.averageAge().toFixed(1)} years`,
            `Age std-dev     : ${ageStdDev.toFixed(2)}`,
            `Available cap   : ${this._shelter.availableCapacity()}`,
            '--- Animal Details ---',
            ...animals.map(a => `  ${a.describe()}`),
        ];
        return lines.join('\n');
    }
}

module.exports = Report;
