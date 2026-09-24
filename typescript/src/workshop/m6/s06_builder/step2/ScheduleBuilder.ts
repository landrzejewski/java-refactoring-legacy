import { IllegalStateError } from '../../../../shared/errors.js';
import { type LocalDate, LocalTime } from '../../../shared/time.js';
import { DaySchedule } from './DaySchedule.js';
import { Hall } from './Hall.js';
import { Screening } from './Screening.js';

/**
 * Krok 2: builder zbiera dane, a drzewo (niezmienne obiekty) powstaje w build(). Builder jest
 * jednorazowy - drugie build() rzuca wyjątek, bo wynik nie może zależeć od późniejszych wywołań.
 */
export class ScheduleBuilder {
  // Map zachowuje kolejność wstawiania (w Javie: LinkedHashMap).
  private readonly halls = new Map<string, Screening[]>();
  private current: Screening[] | null = null;
  private built = false;

  constructor(private readonly date: LocalDate) {}

  hall(name: string): this {
    this.requireNotBuilt();
    if (this.halls.has(name)) {
      throw new IllegalStateError(`duplicate hall: ${name}`);
    }
    this.current = [];
    this.halls.set(name, this.current);
    return this;
  }

  screening(title: string, hour: number, minute: number): this {
    this.requireNotBuilt();
    if (this.current === null) {
      throw new IllegalStateError('screening without hall');
    }
    this.current.push(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }

  build(): DaySchedule {
    this.requireNotBuilt();
    this.built = true;
    const result: Hall[] = [];
    this.halls.forEach((screenings, name) => result.push(new Hall(name, screenings)));
    return new DaySchedule(this.date, result);
  }

  private requireNotBuilt(): void {
    if (this.built) {
      throw new IllegalStateError('builder already used');
    }
  }
}
