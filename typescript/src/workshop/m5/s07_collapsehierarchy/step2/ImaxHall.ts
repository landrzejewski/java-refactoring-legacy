import { Hall } from './Hall.js';

/**
 * Krok 2: już nieużywana (klienci tworzą sale przez Hall.imax). W bibliotece zostałaby tu jako
 * typ zgodności oznaczony `@deprecated` na jedno wydanie.
 */
export class ImaxHall extends Hall {
  constructor(name: string, rows: number, seatsPerRow: number) {
    super(name, rows, seatsPerRow, rows - 1);
  }
}
