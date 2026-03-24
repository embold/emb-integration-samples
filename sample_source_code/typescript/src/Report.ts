import { Shelter }       from './Shelter';
import { AnimalVisitor } from './AnimalVisitor';
import { MathUtils }     from './MathUtils';

/**
 * Report: orchestrates Shelter + AnimalVisitor to produce a formatted summary.
 * Incoming calls from main; outgoing calls to Shelter, AnimalVisitor, MathUtils.
 */
export class Report {
    constructor(private readonly shelter: Shelter) {}

    generate(): string {
        const visitor = new AnimalVisitor();
        const animals = Array.from(this.shelter.getAnimals());
        animals.forEach(a => a.accept(visitor));

        const ages      = animals.map(a => a.age);
        const ageStdDev = MathUtils.standardDeviation(ages);

        const lines: string[] = [
            `=== Shelter Report: ${this.shelter.name} ===`,
            `Total animals   : ${visitor.totalCount}`,
            `Dogs            : ${visitor.dogCount}`,
            `Cats            : ${visitor.catCount}`,
            `Avg health score: ${visitor.averageHealthScore().toFixed(2)}%`,
            `Avg age         : ${this.shelter.averageAge().toFixed(1)} years`,
            `Age std-dev     : ${ageStdDev.toFixed(2)}`,
            `Available cap   : ${this.shelter.availableCapacity()}`,
            '--- Animal Details ---',
            ...animals.map(a => `  ${a.describe()}`),
        ];
        return lines.join('\n');
    }
}
