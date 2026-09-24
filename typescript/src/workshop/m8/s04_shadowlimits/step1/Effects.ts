import type { Money } from '../../../shared/Money.js';

/** Krok 1: port efektów ubocznych - nowa ścieżka nie sięga już bezpośrednio do infrastruktury. */
export interface Effects {
  sendMail(to: string, text: string): void;

  charge(card: string, amount: Money): void;

  save(row: string): void;
}
