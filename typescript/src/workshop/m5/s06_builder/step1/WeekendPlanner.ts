import type { LocalDate } from '../../../shared/time.js';
import type { DaySchedule } from './DaySchedule.js';
import { ScheduleBuilder } from './ScheduleBuilder.js';

/** Krok 1: klient mówi "co" zbudować; kształt kodu przypomina kształt repertuaru. */
export class WeekendPlanner {
  plan(date: LocalDate): DaySchedule {
    const dayOfWeek = date.dayOfWeek;
    const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
    const builder = new ScheduleBuilder(date)
      .hall('Sala 1')
      .screening('Diuna', 18, 0)
      .screening('Diuna', 21, 0)
      .hall('Sala 2');
    if (weekend) {
      builder.screening('Kraina Lodu', 10, 0);
    }
    builder.screening('Amator', 17, 30)
      .hall('Sala 3 VIP');
    if (weekend) {
      builder.screening('Amator', 20, 0);
    }
    return builder.build();
  }
}
