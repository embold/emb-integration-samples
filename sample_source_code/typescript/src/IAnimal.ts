import type { IVisitor } from './IVisitor';

/** Interface defining the contract for all animals. */
export interface IAnimal {
    readonly name:    string;
    readonly species: string;
    readonly age:     number;
    readonly weight:  number;
    sound():   string;
    describe(): string;
    /** Accept a visitor — enables typed double-dispatch. */
    accept(visitor: IVisitor): void;
}
