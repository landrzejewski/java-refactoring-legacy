import { IllegalStateError } from '../../../shared/errors.js';

/**
 * Świat zewnętrzny sceny (stabilny): "baza danych" z wierszami unknown[].
 * Niedostępna baza rzuca `IllegalStateError` przy zapisie.
 */
export class RowStore {
  private readonly rows: Array<readonly unknown[]> = [];

  constructor(private readonly available: boolean) {}

  /** Zapisuje wiersz i zwraca wygenerowany identyfikator R-n. */
  insert(columns: readonly unknown[]): string {
    if (!this.available) {
      throw new IllegalStateError('baza niedostepna');
    }
    this.rows.push([...columns]);
    return `R-${this.rows.length}`;
  }

  dump(): string[] {
    return this.rows.map((row) => row.map((column) => String(column)).join(';'));
  }
}
