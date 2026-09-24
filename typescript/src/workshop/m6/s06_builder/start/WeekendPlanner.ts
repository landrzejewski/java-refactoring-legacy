import { type LocalDate, LocalTime } from '../../../shared/time.js';
import { DaySchedule } from './DaySchedule.js';
import { Hall } from './Hall.js';
import { Screening } from './Screening.js';

/**
 * Start: klient buduje drzewo dzień - sala - seans ręcznie. Dużo new/add, łatwo zapomnieć
 * day.add(hall), a kod nie przypomina kształtu repertuaru.
 */
export class WeekendPlanner {
  plan(date: LocalDate): DaySchedule {
    const dayOfWeek = date.dayOfWeek;
    const weekend = dayOfWeek === 'SATURDAY' || dayOfWeek === 'SUNDAY';
    const day = new DaySchedule(date);
    const hall1 = new Hall('Sala 1');
    hall1.add(new Screening('Diuna', LocalTime.of(18, 0)));
    hall1.add(new Screening('Diuna', LocalTime.of(21, 0)));
    day.add(hall1);
    const hall2 = new Hall('Sala 2');
    if (weekend) {
      hall2.add(new Screening('Kraina Lodu', LocalTime.of(10, 0)));
    }
    hall2.add(new Screening('Amator', LocalTime.of(17, 30)));
    day.add(hall2);
    const hall3 = new Hall('Sala 3 VIP');
    if (weekend) {
      hall3.add(new Screening('Amator', LocalTime.of(20, 0)));
    }
    day.add(hall3);
    return day;
  }
}
