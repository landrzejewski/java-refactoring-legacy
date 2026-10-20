import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { BookingLedger } from './BookingLedger.js';

/**
 * Krok 3 (bez zmian od kroku 2): Command - żądanie kasjera jako obiekt. Wykonanie, cofnięcie i wpis do dziennika
 * są w jednym miejscu; polecenie pamięta tylko to, czego potrzebuje do cofnięcia.
 */
export interface DeskCommand {
  execute(ledger: BookingLedger): string;

  undo(ledger: BookingLedger): void;

  journalEntry(): string;
}

/** Wspólna walidacja poleceń "OPERACJA <id>" (w Javie: metoda statyczna interfejsu). */
export function idFrom(args: readonly string[], operation: string): string {
  const id = args[1];
  if (id === undefined) {
    throw new IllegalArgumentError(`${operation} <id>`);
  }
  return id;
}
