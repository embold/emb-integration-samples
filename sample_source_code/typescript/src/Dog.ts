import { Animal }   from './Animal';
import type { IVisitor } from './IVisitor';
import { MathUtils } from './MathUtils';

const BREED_IDEAL_WEIGHT: Record<string, number> = {
    'chihuahua':       2.5,
    'labrador':        30.0,
    'german shepherd': 35.0,
};

/** Concrete Dog with breed-specific health scoring. */
export class Dog extends Animal {
    constructor(
        name:  string,
        age:   number,
        public readonly breed: string,
        weight: number,
    ) {
        super(name, age, 'Canis lupus familiaris', weight);
    }

    sound(): string             { return 'Woof'; }
    accept(v: IVisitor): void   { v.visitDog(this); }

    /**
     * Computes a health score (0-100) based on age and weight vs breed ideal.
     * Outgoing calls to MathUtils.
     */
    computeHealthScore(): number {
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
