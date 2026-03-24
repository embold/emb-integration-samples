import type { IAnimal } from './IAnimal';

/** Shelter: composition holding IAnimal instances. */
export class Shelter {
    private readonly _animals: IAnimal[] = [];

    constructor(
        public readonly name:     string,
        private readonly capacity: number,
    ) {}

    /** Returns false if at capacity. */
    intake(animal: IAnimal): boolean {
        if (this._animals.length >= this.capacity) return false;
        this._animals.push(animal);
        return true;
    }

    /** Removes and returns the named animal, or null. */
    adopt(animalName: string): IAnimal | null {
        const idx = this._animals.findIndex(
            a => a.name.toLowerCase() === animalName.toLowerCase()
        );
        if (idx === -1) return null;
        return this._animals.splice(idx, 1)[0];
    }

    getAnimals():        readonly IAnimal[] { return [...this._animals]; }
    availableCapacity(): number { return this.capacity - this._animals.length; }
    averageAge():        number {
        if (!this._animals.length) return 0;
        return this._animals.reduce((s, a) => s + a.age, 0) / this._animals.length;
    }
}
