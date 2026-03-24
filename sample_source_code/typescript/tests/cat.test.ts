import { Cat } from '../src/Cat';
import type { IVisitor } from '../src/IVisitor';
import type { Dog } from '../src/Dog';

class MockVisitor implements IVisitor {
  visitDogCalled = false;
  visitCatCalled = false;
  visitDog(_dog: Dog): void { this.visitDogCalled = true; }
  visitCat(_cat: Cat): void { this.visitCatCalled = true; }
}

describe('Cat properties', () => {
  const whiskers = new Cat('Whiskers', 5, true, 4.5);

  test('name returns correct value', () => {
    expect(whiskers.name).toBe('Whiskers');
  });
  test('species returns Felis catus', () => {
    expect(whiskers.species).toBe('Felis catus');
  });
  test('age returns correct value', () => {
    expect(whiskers.age).toBe(5);
  });
  test('weight returns correct value', () => {
    expect(whiskers.weight).toBe(4.5);
  });
  test('isIndoor is accessible', () => {
    expect(whiskers.isIndoor).toBe(true);
  });
  test('sound returns Meow', () => {
    expect(whiskers.sound()).toBe('Meow');
  });
  test('describe contains name and sound', () => {
    const d = whiskers.describe();
    expect(d).toContain('Whiskers');
    expect(d).toContain('Meow');
  });
  test('toString equals describe', () => {
    expect(whiskers.toString()).toBe(whiskers.describe());
  });
});

describe('Cat.expectedLifespan', () => {
  test('indoor cat at ideal weight returns 15', () => {
    expect(new Cat('Indoor', 3, true, 4.5).expectedLifespan()).toBe(15);
  });
  test('outdoor cat at ideal weight returns 10', () => {
    expect(new Cat('Outdoor', 3, false, 4.5).expectedLifespan()).toBe(10);
  });
  test('indoor cat lives longer than outdoor cat at same weight', () => {
    const indoor  = new Cat('Indoor',  3, true,  4.5);
    const outdoor = new Cat('Outdoor', 3, false, 4.5);
    expect(indoor.expectedLifespan()).toBeGreaterThan(outdoor.expectedLifespan());
  });
  test('overweight indoor cat has shorter lifespan than ideal-weight', () => {
    const ideal = new Cat('Ideal', 3, true, 4.5);
    const heavy = new Cat('Heavy', 3, true, 9.0);
    expect(heavy.expectedLifespan()).toBeLessThan(ideal.expectedLifespan());
  });
  test('lifespan never falls below 5', () => {
    expect(new Cat('VeryHeavy', 3, false, 30.0).expectedLifespan()).toBeGreaterThanOrEqual(5);
  });
});

describe('Cat.accept (visitor dispatch)', () => {
  test('routes to visitCat, not visitDog', () => {
    const cat  = new Cat('Luna', 2, true, 3.9);
    const mock = new MockVisitor();
    cat.accept(mock);
    expect(mock.visitCatCalled).toBe(true);
    expect(mock.visitDogCalled).toBe(false);
  });
});
