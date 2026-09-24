import type { GateInput } from '../GateInput.js';

/**
 * Start: "bramka jakości" to lista kontrolna w wiki i dobra wola. Metoda evaluate niczego
 * nie sprawdza, więc przepuszcza wszystko - fałszywe poczucie bezpieczeństwa.
 * - testy zielone
 * - brak ostrzeżeń kompilatora
 * - brak TODO i console w domenie
 * - kluczowa klasa pokryta testem
 */
export class QualityGate {
  /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
  evaluate(_input: GateInput): string[] {
    return [];
  }

  passes(input: GateInput): boolean {
    return this.evaluate(input).length === 0;
  }
}
