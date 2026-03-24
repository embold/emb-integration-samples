'use strict';

const Shelter = require('../src/Shelter');
const Dog     = require('../src/Dog');
const Cat     = require('../src/Cat');

describe('Shelter.intake', () => {
  test('intake returns true and adds animal', () => {
    const shelter = new Shelter('Test', 5);
    expect(shelter.intake(new Dog('Rex', 3, 'Labrador', 30))).toBe(true);
    expect(shelter.getAnimals()).toHaveLength(1);
  });
  test('intake returns false when at capacity', () => {
    const shelter = new Shelter('Small', 1);
    shelter.intake(new Dog('Rex', 3, 'Labrador', 30));
    expect(shelter.intake(new Dog('Max', 5, 'Labrador', 30))).toBe(false);
    expect(shelter.getAnimals()).toHaveLength(1);
  });
  test('multiple intakes up to capacity all succeed', () => {
    const shelter = new Shelter('Medium', 3);
    expect(shelter.intake(new Dog('A', 1, 'Labrador', 30))).toBe(true);
    expect(shelter.intake(new Dog('B', 2, 'Labrador', 30))).toBe(true);
    expect(shelter.intake(new Dog('C', 3, 'Labrador', 30))).toBe(true);
    expect(shelter.getAnimals()).toHaveLength(3);
  });
});

describe('Shelter.adopt', () => {
  let shelter;

  beforeEach(() => {
    shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex', 3, 'Labrador', 30));
    shelter.intake(new Cat('Luna', 2, true, 4.0));
  });

  test('adopt returns the correct animal', () => {
    const adopted = shelter.adopt('Rex');
    expect(adopted).not.toBeNull();
    expect(adopted.getName()).toBe('Rex');
  });
  test('adopted animal is removed from shelter', () => {
    shelter.adopt('Rex');
    expect(shelter.getAnimals()).toHaveLength(1);
    expect(shelter.getAnimals()[0].getName()).toBe('Luna');
  });
  test('adopt is case-insensitive', () => {
    const adopted = shelter.adopt('rex');
    expect(adopted.getName()).toBe('Rex');
  });
  test('adopt returns null for unknown name', () => {
    expect(shelter.adopt('Unknown')).toBeNull();
  });
  test('shelter unchanged after failed adopt', () => {
    shelter.adopt('Unknown');
    expect(shelter.getAnimals()).toHaveLength(2);
  });
});

describe('Shelter.getAnimals', () => {
  test('returns a defensive copy — modifying it does not affect shelter', () => {
    const shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex', 3, 'Labrador', 30));
    const copy = shelter.getAnimals();
    copy.pop();
    expect(shelter.getAnimals()).toHaveLength(1);
  });
  test('returns empty array for new shelter', () => {
    expect(new Shelter('Empty', 10).getAnimals()).toHaveLength(0);
  });
});

describe('Shelter.availableCapacity', () => {
  test('full capacity available when empty', () => {
    expect(new Shelter('Test', 10).availableCapacity()).toBe(10);
  });
  test('decreases by 1 per intake', () => {
    const shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex', 3, 'Labrador', 30));
    expect(shelter.availableCapacity()).toBe(9);
  });
  test('increases by 1 after successful adopt', () => {
    const shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex', 3, 'Labrador', 30));
    shelter.adopt('Rex');
    expect(shelter.availableCapacity()).toBe(10);
  });
});

describe('Shelter.averageAge', () => {
  test('returns 0 for empty shelter', () => {
    expect(new Shelter('Empty', 10).averageAge()).toBe(0);
  });
  test('returns age of single animal', () => {
    const shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex', 4, 'Labrador', 30));
    expect(shelter.averageAge()).toBe(4);
  });
  test('returns correct mean age for multiple animals', () => {
    const shelter = new Shelter('Test', 10);
    shelter.intake(new Dog('Rex',  4, 'Labrador', 30));
    shelter.intake(new Cat('Luna', 2, true, 4.0));
    expect(shelter.averageAge()).toBe(3);
  });
});
