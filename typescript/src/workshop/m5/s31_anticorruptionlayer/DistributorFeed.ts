import type { DistributorRecord } from './DistributorRecord.js';

/** Klient systemu dystrybutora (nie zmieniamy go): zwraca rekord albo null, gdy filmu nie zna. */
export class DistributorFeed {
  private readonly records = new Map<string, DistributorRecord>();

  constructor(records: readonly DistributorRecord[]) {
    records.forEach((record) => this.records.set(record.filmCode, record));
  }

  fetch(filmCode: string): DistributorRecord | null {
    return this.records.get(filmCode) ?? null;
  }
}
