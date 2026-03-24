'use strict';

const AnimalVisitor = require('../src/AnimalVisitor');
const Dog           = require('../src/Dog');
const Cat           = require('../src/Cat');

describe('AnimalVisitor initial state', () => {
  let visitor;
  beforeEach(() => { visitor = new AnimalVisitor(); });

  test('dogCount starts at 0', () => {
    expect(visitor.dogCount).toBe(0);
  });
  test('catCount starts at 0', () => {
    expect(visitor.catCount).toBe(0);
  });
  test('totalCount starts at 0', () => {
    expect(visitor.totalCount).toBe(0);
  });
  test('averageHealthScore returns 0 with no visits', () => {
    expect(visitor.averageHealthScore()).toBe(0);
  });
});

describe('AnimalVisitor.visitDog', () => {
  test('increments dogCount', () => {
    const visitor = new AnimalVisitor();
    visitor.visitDog(new Dog('Rex', 3, 'Labrador', 30));
    expect(visitor.dogCount).toBe(1);
    expect(visitor.catCount).toBe(0);
  });
  test('adds to totalHealth (averageHealthScore > 0)', () => {
    const visitor = new AnimalVisitor();
    visitor.visitDog(new Dog('Rex', 3, 'Labrador', 30));
    expect(visitor.averageHealthScore()).toBeGreaterThan(0);
  });
});

describe('AnimalVisitor.visitCat', () => {
  test('increments catCount', () => {
    const visitor = new AnimalVisitor();
    visitor.visitCat(new Cat('Luna', 2, true, 4.5));
    expect(visitor.catCount).toBe(1);
    expect(visitor.dogCount).toBe(0);
  });
  test('adds to totalHealth (averageHealthScore > 0)', () => {
    const visitor = new AnimalVisitor();
    visitor.visitCat(new Cat('Luna', 2, true, 4.5));
    expect(visitor.averageHealthScore()).toBeGreaterThan(0);
  });
});

describe('AnimalVisitor mixed visits', () => {
  test('totalCount reflects both dogs and cats', () => {
    const visitor = new AnimalVisitor();
    visitor.visitDog(new Dog('Rex',  3, 'Labrador', 30));
    visitor.visitCat(new Cat('Luna', 2, true, 4.5));
    expect(visitor.totalCount).toBe(2);
    expect(visitor.dogCount).toBe(1);
    expect(visitor.catCount).toBe(1);
  });
  test('averageHealthScore is between 0 and 100', () => {
    const visitor = new AnimalVisitor();
    visitor.visitDog(new Dog('Rex',  3, 'Labrador', 30));
    visitor.visitCat(new Cat('Luna', 2, true, 4.5));
    expect(visitor.averageHealthScore()).toBeGreaterThan(0);
    expect(visitor.averageHealthScore()).toBeLessThanOrEqual(100);
  });
});

describe('Double-dispatch via accept()', () => {
  test('Dog.accept routes to visitDog', () => {
    const visitor = new AnimalVisitor();
    new Dog('Buddy', 3, 'Labrador', 28.5).accept(visitor);
    expect(visitor.dogCount).toBe(1);
    expect(visitor.catCount).toBe(0);
  });
  test('Cat.accept routes to visitCat', () => {
    const visitor = new AnimalVisitor();
    new Cat('Whiskers', 5, true, 4.2).accept(visitor);
    expect(visitor.catCount).toBe(1);
    expect(visitor.dogCount).toBe(0);
  });
});
