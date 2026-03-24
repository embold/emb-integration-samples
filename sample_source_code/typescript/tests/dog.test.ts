import { Dog } from '../src/Dog';
import type { IVisitor } from '../src/IVisitor';
import type { Cat } from '../src/Cat';

/** Minimal visitor stub for dispatch tests. */
class MockVisitor implements IVisitor {
  visitDogCalled = false;
  visitCatCalled = false;
  visitDog(_dog: Dog): void { this.visitDogCalled = true; }
  visitCat(_cat: Cat): void { this.visitCatCalled = true; }
}

describe('Dog properties', () => {
  const rex = new Dog('Rex', 3, 'Labrador', 30.0);

  test('name returns correct value', () => {
    expect(rex.name).toBe('Rex');
  });
  test('species returns Canis lupus familiaris', () => {
    expect(rex.species).toBe('Canis lupus familiaris');
  });
  test('age returns correct value', () => {
    expect(rex.age).toBe(3);
  });
  test('weight returns correct value', () => {
    expect(rex.weight).toBe(30.0);
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
    expect(new Dog('Puppy', 1, 'Labrador', 30.0).computeHealthScore()).toBeGreaterThan(90);
  });
  test('prime dog (age <= 7) at ideal weight has score > 85', () => {
    expect(new Dog('Prime', 5, 'Labrador', 30.0).computeHealthScore()).toBeGreaterThan(85);
  });
  test('senior dog (age > 7) scores lower than prime dog', () => {
    const prime  = new Dog('Prime',  5, 'Labrador', 30.0);
    const senior = new Dog('Senior', 10, 'Labrador', 30.0);
    expect(senior.computeHealthScore()).toBeLessThan(prime.computeHealthScore());
  });
  test('overweight dog scores lower than ideal-weight dog', () => {
    const ideal = new Dog('Ideal', 3, 'Labrador', 30.0);
    const heavy = new Dog('Heavy', 3, 'Labrador', 50.0);
    expect(heavy.computeHealthScore()).toBeLessThan(ideal.computeHealthScore());
  });
  test('unknown breed uses default ideal weight', () => {
    const score = new Dog('Mixed', 3, 'Unknown Breed', 15.0).computeHealthScore();
    expect(score).toBeGreaterThanOrEqual(0);
    expect(score).toBeLessThanOrEqual(100);
  });
  test('score is always clamped to [0, 100]', () => {
    const score = new Dog('Extreme', 15, 'Labrador', 100.0).computeHealthScore();
    expect(score).toBeGreaterThanOrEqual(0);
    expect(score).toBeLessThanOrEqual(100);
  });
  test('breed matching is case-insensitive', () => {
    const lower = new Dog('Rex', 3, 'labrador', 30.0);
    const upper = new Dog('Rex', 3, 'LABRADOR', 30.0);
    expect(lower.computeHealthScore()).toBeCloseTo(upper.computeHealthScore(), 5);
  });
});

describe('Dog.accept (visitor dispatch)', () => {
  test('routes to visitDog, not visitCat', () => {
    const dog = new Dog('Buddy', 3, 'Labrador', 28.5);
    const mock = new MockVisitor();
    dog.accept(mock);
    expect(mock.visitDogCalled).toBe(true);
    expect(mock.visitCatCalled).toBe(false);
  });
});
