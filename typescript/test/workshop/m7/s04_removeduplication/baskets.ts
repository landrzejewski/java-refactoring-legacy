import { Decimal } from 'decimal.js';

// Koszyki wspólne dla S04EquivalenceTest i S04RoundingDecisionTest
// (w Javie statyczne pola pakietowe klas testowych).

export function prices(price: string, count: number): Decimal[] {
  return Array.from({ length: count }, () => new Decimal(price));
}

export const TWO_3D = prices('32.00', 2);
export const TEN_2D = prices('25.00', 10);

// 3 x student 2D (18.75) + 7 x normalny (25.00) = 231.25, rabat 23.125.
export const EDGE: readonly Decimal[] = Object.freeze([...prices('18.75', 3), ...prices('25.00', 7)]);
