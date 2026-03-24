import { MathUtils } from '../src/MathUtils';

describe('MathUtils.clamp', () => {
  test('value within range is returned unchanged', () => {
    expect(MathUtils.clamp(5, 0, 10)).toBe(5);
  });
  test('value below min is clamped to min', () => {
    expect(MathUtils.clamp(-5, 0, 10)).toBe(0);
  });
  test('value above max is clamped to max', () => {
    expect(MathUtils.clamp(15, 0, 10)).toBe(10);
  });
  test('value at min boundary stays at min', () => {
    expect(MathUtils.clamp(0, 0, 10)).toBe(0);
  });
  test('value at max boundary stays at max', () => {
    expect(MathUtils.clamp(10, 0, 10)).toBe(10);
  });
  test('works with negative bounds', () => {
    expect(MathUtils.clamp(-3, -10, -1)).toBe(-3);
  });
});

describe('MathUtils.linearDecay', () => {
  test('returns 0 when current is 0', () => {
    expect(MathUtils.linearDecay(0, 10, 100)).toBe(0);
  });
  test('returns maxPenalty when current equals max', () => {
    expect(MathUtils.linearDecay(10, 10, 100)).toBe(100);
  });
  test('returns half penalty at midpoint', () => {
    expect(MathUtils.linearDecay(5, 10, 100)).toBe(50);
  });
  test('returns 0 when max is 0', () => {
    expect(MathUtils.linearDecay(5, 0, 100)).toBe(0);
  });
  test('result is clamped and does not exceed maxPenalty', () => {
    expect(MathUtils.linearDecay(20, 10, 50)).toBe(50);
  });
});

describe('MathUtils.fibonacci', () => {
  test.each<[number, number]>([
    [0,  0],
    [1,  1],
    [2,  1],
    [3,  2],
    [5,  5],
    [10, 55],
    [20, 6765],
  ])('fibonacci(%i) === %i', (n, expected) => {
    expect(MathUtils.fibonacci(n)).toBe(expected);
  });
});

describe('MathUtils.standardDeviation', () => {
  test('returns 0 for empty array', () => {
    expect(MathUtils.standardDeviation([])).toBe(0);
  });
  test('returns 0 for single-element array', () => {
    expect(MathUtils.standardDeviation([5])).toBe(0);
  });
  test('all identical values yields 0', () => {
    expect(MathUtils.standardDeviation([3, 3, 3])).toBe(0);
  });
  test('computes correct population std dev', () => {
    expect(MathUtils.standardDeviation([2, 4, 4, 4, 5, 5, 7, 9])).toBeCloseTo(2, 5);
  });
  test('two-element symmetric array', () => {
    expect(MathUtils.standardDeviation([0, 10])).toBeCloseTo(5, 5);
  });
});

describe('MathUtils.factorial', () => {
  test('factorial(0) === 1', () => {
    expect(MathUtils.factorial(0)).toBe(1);
  });
  test('factorial(1) === 1', () => {
    expect(MathUtils.factorial(1)).toBe(1);
  });
  test('factorial(5) === 120', () => {
    expect(MathUtils.factorial(5)).toBe(120);
  });
  test('factorial(10) === 3628800', () => {
    expect(MathUtils.factorial(10)).toBe(3628800);
  });
  test('throws on negative input', () => {
    expect(() => MathUtils.factorial(-1)).toThrow('Negative input to factorial');
  });
});
