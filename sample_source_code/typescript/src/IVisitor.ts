import type { Dog } from './Dog';
import type { Cat } from './Cat';

/** Visitor interface for typed double-dispatch operations on animals. */
export interface IVisitor {
    visitDog(dog: Dog): void;
    visitCat(cat: Cat): void;
}
