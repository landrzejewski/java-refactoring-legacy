import { IllegalStateError } from '../../../../shared/errors.js';
import { type LocalDate, LocalTime } from '../../../shared/time.js';
import { DaySchedule } from './DaySchedule.js';
import { Hall } from './Hall.js';
import { Screening } from './Screening.js';

/**
 * Krok 1: klasyczny Builder (Encapsulate Composite with Builder) - pamięta bieżącą salę,
 * więc klient nie operuje węzłami ani add. Drzewo pod spodem bez zmian.
 */
export class ScheduleBuilder {
  private readonly day: DaySchedule;
  private current: Hall | null = null;

  constructor(date: LocalDate) {
    this.day = new DaySchedule(date);
  }

  hall(name: string): this {
    this.current = new Hall(name);
    this.day.add(this.current);
    return this;
  }

  screening(title: string, hour: number, minute: number): this {
    if (this.current === null) {
      throw new IllegalStateError('screening without hall');
    }
    this.current.add(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }

  build(): DaySchedule {
    return this.day;
  }
}
