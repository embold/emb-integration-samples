'use strict';

const Report  = require('../src/Report');
const Shelter = require('../src/Shelter');
const Dog     = require('../src/Dog');
const Cat     = require('../src/Cat');

function buildShelter() {
  const shelter = new Shelter('Happy Paws', 20);
  shelter.intake(new Dog('Buddy',    3, 'Labrador',        28.5));
  shelter.intake(new Dog('Max',      7, 'German Shepherd', 33.0));
  shelter.intake(new Cat('Whiskers', 5, true,   4.2));
  shelter.intake(new Cat('Shadow',  10, false,  5.8));
  return shelter;
}

describe('Report.generate', () => {
  test('includes the shelter name', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Happy Paws');
  });
  test('includes total-animals count', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Total animals   : 4');
  });
  test('includes dog count', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Dogs            : 2');
  });
  test('includes cat count', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Cats            : 2');
  });
  test('includes available capacity', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Available cap   : 16');
  });
  test('lists animal names in details section', () => {
    const report = new Report(buildShelter()).generate();
    expect(report).toContain('Buddy');
    expect(report).toContain('Whiskers');
  });
  test('generate is idempotent', () => {
    const r = new Report(buildShelter());
    expect(r.generate()).toBe(r.generate());
  });
  test('empty shelter produces zero counts', () => {
    const report = new Report(new Shelter('Empty', 5)).generate();
    expect(report).toContain('Total animals   : 0');
    expect(report).toContain('Dogs            : 0');
    expect(report).toContain('Cats            : 0');
  });
});
