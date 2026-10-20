import { IllegalArgumentError } from '../../../shared/errors.js';

// Odpowiednik Integer.parseInt z Javy: tylko liczba całkowita dziesiętna, inaczej wyjątek
// z tym samym komunikatem (NumberFormatException jest w Javie podklasą IllegalArgumentException).
// Number.parseInt z JavaScriptu zwróciłby NaN albo po cichu uciął "3x" do 3.
export function parseInteger(text: string): number {
  if (!/^[+-]?\d+$/.test(text)) {
    throw new IllegalArgumentError(`For input string: "${text}"`);
  }
  return Number(text);
}
