import type { LocalDate } from '../../../shared/time.js';

/** Krok 2: Move Method - typ przyciąga zachowanie: opis terminu należy do terminu. */
export class ScreeningSlot {
  constructor(
    readonly screeningId: string,
    readonly date: LocalDate,
    readonly hall: number,
    readonly format: string,
  ) {}

  label(): string {
    return `${this.screeningId} ${this.date.toString()} sala ${this.hall} (${this.format})`;
  }
}
