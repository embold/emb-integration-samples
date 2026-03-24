import { Report }  from '../src/Report';
import { Shelter } from '../src/Shelter';
import { Dog }     from '../src/Dog';
import { Cat }     from '../src/Cat';

function buildShelter(): Shelter {
  const shelter = new Shelter('Happy Paws', 20);
  shelter.intake(new Dog('Buddy',    3, 'Labrador',        28.5));
  shelter.intake(new Dog('Max',      7, 'German Shepherd', 33.0));
  shelter.intake(new Cat('Whiskers', 5, true,   4.2));
  shelter.intake(new Cat('Shadow',  10, false,  5.8));
  return shelter;
}

describe('Report.generate', () => {
  test('includes the shelter name', () => {
    expect(new Report(buildShelter()).generate()).toContain('Happy Paws');
  });
  test('includes total-animals count', () => {
    expect(new Report(buildShelter()).generate()).toContain('Total animals   : 4');
  });
  test('includes dog count', () => {
    expect(new Report(buildShelter()).generate()).toContain('Dogs            : 2');
  });
  test('includes cat count', () => {
    expect(new Report(buildShelter()).generate()).toContain('Cats            : 2');
  });
  test('includes available capacity', () => {
    expect(new Report(buildShelter()).generate()).toContain('Available cap   : 16');
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
