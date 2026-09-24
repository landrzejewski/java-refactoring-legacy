import type { Money } from '../../../shared/Money.js';

/** Krok 2 (bez zmian): port efektów ubocznych nowej ścieżki. */
export interface Effects {
  sendMail(to: string, text: string): void;

  charge(card: string, amount: Money): void;

  save(row: string): void;
}
