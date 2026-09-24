import type { Money } from '../../../shared/Money.js';

/** Krok 3 (bez zmian): port efektów ubocznych - używany tylko przy wykonaniu planu. */
export interface Effects {
  sendMail(to: string, text: string): void;

  charge(card: string, amount: Money): void;

  save(row: string): void;
}
