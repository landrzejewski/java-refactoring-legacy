import type { LocalTime } from '../../../shared/time.js';
import type { ScreeningSchedule } from './ScreeningSchedule.js';

/** Tablica zależy tylko od roli ScreeningSchedule. */
export class ScheduleBoard {
  constructor(private readonly backOffice: ScreeningSchedule) {}

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
