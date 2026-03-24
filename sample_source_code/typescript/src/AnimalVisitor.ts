import type { IVisitor } from './IVisitor';
import { Dog }           from './Dog';
import { Cat }           from './Cat';
import { MathUtils }     from './MathUtils';

/**
 * Concrete visitor: accumulates health stats while traversing animals.
 * Demonstrates typed double-dispatch via visitDog / visitCat.
 */
export class AnimalVisitor implements IVisitor {
    private _dogCount    = 0;
    private _catCount    = 0;
    private _totalHealth = 0;

    visitDog(dog: Dog): void {
        this._dogCount++;
        this._totalHealth += dog.computeHealthScore();
    }

    visitCat(cat: Cat): void {
        this._catCount++;
        const proxy = MathUtils.clamp(100 - Math.abs(cat.weight - 4.5) * 10, 0, 100);
        this._totalHealth += proxy;
    }

    get dogCount():   number { return this._dogCount; }
    get catCount():   number { return this._catCount; }
    get totalCount(): number { return this._dogCount + this._catCount; }

    averageHealthScore(): number {
        return this.totalCount === 0
            ? 0
            : Math.round((this._totalHealth / this.totalCount) * 100) / 100;
    }
}
