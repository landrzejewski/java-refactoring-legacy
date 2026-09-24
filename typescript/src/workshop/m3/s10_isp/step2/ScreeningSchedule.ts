import type { LocalTime } from '../../../shared/time.js';

/** Rola z perspektywy tablicy seansów. */
export interface ScreeningSchedule {
  scheduleScreening(title: string, start: LocalTime): void;

  cancelScreening(title: string): void;

  screenings(): string[];
}
