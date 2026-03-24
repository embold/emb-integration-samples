'use strict';

const Dog = require('../src/Dog');

describe('Dog properties', () => {
  const rex = new Dog('Rex', 3, 'Labrador', 30.0);

  test('getName returns correct name', () => {
    expect(rex.getName()).toBe('Rex');
  });
  test('getSpecies returns Canis lupus familiaris', () => {
    expect(rex.getSpecies()).toBe('Canis lupus familiaris');
  });
  test('getAge returns correct age', () => {
    expect(rex.getAge()).toBe(3);
  });
  test('getWeight returns correct weight', () => {
    expect(rex.getWeight()).toBe(30.0);
  });
  test('breed is accessible', () => {
    expect(rex.breed).toBe('Labrador');
  });
  test('sound returns Woof', () => {
    expect(rex.sound()).toBe('Woof');
  });
  test('describe contains name and sound', () => {
    const d = rex.describe();
    expect(d).toContain('Rex');
    expect(d).toContain('Woof');
  });
  test('toString equals describe', () => {
    expect(rex.toString()).toBe(rex.describe());
  });
});

describe('Dog.computeHealthScore', () => {
  test('young dog (age <= 2) at ideal weight has score > 90', () => {
    const dog = new Dog('Puppy', 1, 'Labrador', 30.0);
    expect(dog.computeHealthScore()).toBeGreaterThan(90);
  });
  test('prime dog (age <= 7) at ideal weight has score > 85', () => {
    const dog = new Dog('Prime', 5, 'Labrador', 30.0);
    expect(dog.computeHealthScore()).toBeGreaterThan(85);
  });
  test('senior dog (age > 7) has a lower score than prime dog', () => {
    const prime  = new Dog('Prime',  5, 'Labrador', 30.0);
    const senior = new Dog('Senior', 10, 'Labrador', 30.0);
    expect(senior.computeHealthScore()).toBeLessThan(prime.computeHealthScore());
  });
  test('overweight dog scores lower than ideal-weight dog', () => {
    const ideal = new Dog('Ideal', 3, 'Labrador', 30.0);
    const heavy = new Dog('Heavy', 3, 'Labrador', 50.0);
    expect(heavy.computeHealthScore()).toBeLessThan(ideal.computeHealthScore());
  });
  test('unknown breed falls back to default ideal weight', () => {
    const dog = new Dog('Mixed', 3, 'Unknown Breed', 15.0);
    const score = dog.computeHealthScore();
    expect(score).toBeGreaterThanOrEqual(0);
    expect(score).toBeLessThanOrEqual(100);
  });
  test('score is always clamped to [0, 100]', () => {
    const dog = new Dog('Extreme', 15, 'Labrador', 100.0);
    const score = dog.computeHealthScore();
    expect(score).toBeGreaterThanOrEqual(0);
    expect(score).toBeLessThanOrEqual(100);
  });
  test('all breed names are compared case-insensitively', () => {
    const lower = new Dog('Rex', 3, 'labrador',  30.0);
    const upper = new Dog('Rex', 3, 'LABRADOR',  30.0);
    expect(lower.computeHealthScore()).toBeCloseTo(upper.computeHealthScore(), 5);
  });
});

describe('Dog.accept (visitor dispatch)', () => {
  test('calls visitDog on the visitor with the dog instance', () => {
    const dog = new Dog('Buddy', 3, 'Labrador', 28.5);
    const visitor = { visitDog: jest.fn(), visitCat: jest.fn() };
    dog.accept(visitor);
    expect(visitor.visitDog).toHaveBeenCalledWith(dog);
    expect(visitor.visitCat).not.toHaveBeenCalled();
  });
});
