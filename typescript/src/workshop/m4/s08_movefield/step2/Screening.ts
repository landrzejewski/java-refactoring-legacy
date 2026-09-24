import type { Hall } from './Hall.js';

/**
 * Krok 2: Move Field - `vipFromRow` przeniesione do Hall. Screening nie ma już pola,
 * a jego getter deleguje do sali. Bez okresu przejściowego z dwiema kopiami (dual write).
 * Konstruktor stracił parametr - to zmiana dla wszystkich miejsc tworzących seanse.
 */
export class Screening {
  constructor(
    readonly hall: Hall,
    readonly format: number,
  ) {}

  get vipFromRow(): number {
    return this.hall.vipFromRow;
  }

  isVip(row: number): boolean {
    return row >= this.vipFromRow;
  }
}
