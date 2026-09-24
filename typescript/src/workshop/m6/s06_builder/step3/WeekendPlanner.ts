import type { LocalDate } from '../../../shared/time.js';
import type { DaySchedule } from './DaySchedule.js';
import { ScheduleBuilder } from './ScheduleBuilder.js';

/** Krok 3: wcięcia kodu odpowiadają poziomom drzewa dzień - sala - seans. */
export class WeekendPlanner {
  plan(date: LocalDate): DaySchedule {
    const dayOfWeek = date.dayOfWeek;
    const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
    return ScheduleBuilder.day(date)
      .hall('Sala 1', (hall) => hall
        .screening('Diuna', 18, 0)
        .screening('Diuna', 21, 0))
      .hall('Sala 2', (hall) => {
        if (weekend) {
          hall.screening('Kraina Lodu', 10, 0);
        }
        hall.screening('Amator', 17, 30);
      })
      .hall('Sala 3 VIP', (hall) => {
        if (weekend) {
          hall.screening('Amator', 20, 0);
        }
      })
      .build();
  }
}
