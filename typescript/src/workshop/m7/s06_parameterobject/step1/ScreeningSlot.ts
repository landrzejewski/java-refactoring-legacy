import type { LocalDate } from '../../../shared/time.js';

/** Krok 1: Parameter Object nazywa pojęcie "termin seansu w sali". Stan przejściowy: bez walidacji. */
export class ScreeningSlot {
  constructor(
    readonly screeningId: string,
    readonly date: LocalDate,
    readonly hall: number,
    readonly format: string,
  ) {}
}
