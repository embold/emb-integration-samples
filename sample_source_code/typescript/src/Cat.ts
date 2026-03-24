import { Animal }        from './Animal';
import type { IVisitor } from './IVisitor';
import { MathUtils }     from './MathUtils';

/** Concrete Cat with indoor/outdoor lifespan estimation. */
export class Cat extends Animal {
    constructor(
        name:     string,
        age:      number,
        public readonly isIndoor: boolean,
        weight:   number,
    ) {
        super(name, age, 'Felis catus', weight);
    }

    sound(): string           { return 'Meow'; }
    accept(v: IVisitor): void { v.visitCat(this); }

    /** Expected lifespan in years based on environment and weight. */
    expectedLifespan(): number {
        const base       = this.isIndoor ? 15 : 10;
        const deviation  = Math.abs(this._weight - 4.5) / 4.5;
        const adjustment = Math.floor(MathUtils.linearDecay(deviation, 1.0, 5));
        return Math.max(5, base - adjustment);
    }
}
