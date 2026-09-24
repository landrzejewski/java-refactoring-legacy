import type { LocalTime } from '../../../shared/time.js';
import type { CinemaAdminService } from './CinemaAdminService.js';

/** Klient: tablica seansów. Używa scheduleScreening, cancelScreening i screenings. */
export class ScheduleBoard {
  constructor(private readonly backOffice: CinemaAdminService) {}

  plan(title: string, start: LocalTime): void {
    this.backOffice.scheduleScreening(title, start);
  }

  cancel(title: string): void {
    this.backOffice.cancelScreening(title);
  }

  board(): string {
    return this.backOffice.screenings().join(', ');
  }
}
