import { ArithmeticError } from '../shared/errors.js';

// Odpowiedniki Math.addExact/multiplyExact/ceilDiv dla Javy `long`.
// JS `number` przechowuje dokładnie liczby całkowite tylko do
// Number.MAX_SAFE_INTEGER (2^53 - 1), więc to jest nasz „zakres long”:
// wynik spoza niego zgłasza ArithmeticError, jak przepełnienie w Javie.

export function addExact(a: number, b: number): number {
  return requireSafe(a + b);
}

export function multiplyExact(a: number, b: number): number {
  return requireSafe(a * b);
}

// Dzielenie całkowite z zaokrągleniem w górę (Math.ceilDiv) — bez
// sztuczki (a + b - 1) / b, która mogłaby przepełnić zakres.
export function ceilDiv(dividend: number, divisor: number): number {
  const quotient = Math.trunc(dividend / divisor);
  return dividend % divisor !== 0 && (dividend < 0) === (divisor < 0)
    ? quotient + 1
    : quotient;
}

function requireSafe(result: number): number {
  if (!Number.isSafeInteger(result)) {
    throw new ArithmeticError('long overflow');
  }
  return result;
}
