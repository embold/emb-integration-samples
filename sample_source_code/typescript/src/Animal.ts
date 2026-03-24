import type { IAnimal } from './IAnimal';
import type { IVisitor } from './IVisitor';

/**
 * Abstract base class implementing IAnimal.
 * Provides shared state; subclasses must implement sound() and accept().
 */
export abstract class Animal implements IAnimal {
    protected constructor(
        protected readonly _name:    string,
        protected readonly _age:     number,
        protected readonly _species: string,
        protected readonly _weight:  number,
    ) {}

    get name():    string { return this._name; }
    get species(): string { return this._species; }
    get age():     number { return this._age; }
    get weight():  number { return this._weight; }

    abstract sound():              string;
    abstract accept(v: IVisitor):  void;

    describe(): string {
        return `${this._name} (${this._species}), age=${this._age}, ` +
               `weight=${this._weight.toFixed(1)}kg, sound='${this.sound()}'`;
    }

    toString(): string { return this.describe(); }
}
