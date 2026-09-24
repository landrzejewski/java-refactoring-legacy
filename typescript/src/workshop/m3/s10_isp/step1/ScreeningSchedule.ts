import type { LocalTime } from '../../../shared/time.js';

/** Krok 1: rola z perspektywy tablicy seansów. */
export interface ScreeningSchedule {
  scheduleScreening(title: string, start: LocalTime): void;

  cancelScreening(title: string): void;

  screenings(): string[];
}
