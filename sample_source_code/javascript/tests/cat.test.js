'use strict';

const Cat = require('../src/Cat');

describe('Cat properties', () => {
  const whiskers = new Cat('Whiskers', 5, true, 4.5);

  test('getName returns correct name', () => {
    expect(whiskers.getName()).toBe('Whiskers');
  });
  test('getSpecies returns Felis catus', () => {
    expect(whiskers.getSpecies()).toBe('Felis catus');
  });
  test('getAge returns correct age', () => {
    expect(whiskers.getAge()).toBe(5);
  });
  test('getWeight returns correct weight', () => {
    expect(whiskers.getWeight()).toBe(4.5);
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
    const cat = new Cat('Indoor', 3, true, 4.5);
    expect(cat.expectedLifespan()).toBe(15);
  });
  test('outdoor cat at ideal weight returns 10', () => {
    const cat = new Cat('Outdoor', 3, false, 4.5);
    expect(cat.expectedLifespan()).toBe(10);
  });
  test('indoor cat lives longer than outdoor cat (same weight)', () => {
    const indoor  = new Cat('Indoor',  3, true,  4.5);
    const outdoor = new Cat('Outdoor', 3, false, 4.5);
    expect(indoor.expectedLifespan()).toBeGreaterThan(outdoor.expectedLifespan());
  });
  test('overweight indoor cat has reduced lifespan vs ideal-weight', () => {
    const ideal = new Cat('Ideal', 3, true, 4.5);
    const heavy = new Cat('Heavy', 3, true, 9.0);
    expect(heavy.expectedLifespan()).toBeLessThan(ideal.expectedLifespan());
  });
  test('lifespan never falls below 5', () => {
    const cat = new Cat('VeryHeavy', 3, false, 30.0);
    expect(cat.expectedLifespan()).toBeGreaterThanOrEqual(5);
  });
});

describe('Cat.accept (visitor dispatch)', () => {
  test('calls visitCat on the visitor with the cat instance', () => {
    const cat = new Cat('Luna', 2, true, 3.9);
    const visitor = { visitDog: jest.fn(), visitCat: jest.fn() };
    cat.accept(visitor);
    expect(visitor.visitCat).toHaveBeenCalledWith(cat);
    expect(visitor.visitDog).not.toHaveBeenCalled();
  });
});
