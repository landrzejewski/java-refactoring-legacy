import type { Timestamp } from '../../sql/Timestamp.js';

/** Adapter bazy: wiersz tabeli seansów (typy sterownika bazy). */
export class ScreeningRow {
  constructor(readonly table: string, readonly title: string, readonly start: Timestamp) {}

  toString(): string {
    return `ScreeningRow[table=${this.table}, title=${this.title}, start=${this.start.toString()}]`;
  }
}
