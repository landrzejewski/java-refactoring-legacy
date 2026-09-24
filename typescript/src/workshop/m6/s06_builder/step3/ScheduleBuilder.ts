import { IllegalStateError } from '../../../../shared/errors.js';
import { type LocalDate, LocalTime } from '../../../shared/time.js';
import { DaySchedule } from './DaySchedule.js';
import { Hall } from './Hall.js';
import { Screening } from './Screening.js';

/**
 * Builder jednej sali - dostaje go lambda przekazana do ScheduleBuilder.hall.
 * (W Javie: zagnieżdżona klasa ScheduleBuilder.HallBuilder z prywatnym konstruktorem;
 * tu klasa obok, zapisująca seanse do listy należącej do ScheduleBuilder.)
 */
export class HallBuilder {
  constructor(private readonly screenings: Screening[]) {}

  screening(title: string, hour: number, minute: number): this {
    this.screenings.push(new Screening(title, LocalTime.of(hour, minute)));
    return this;
  }
}

/**
 * Krok 3: zagnieżdżony builder - sala konfigurowana lambdą, więc znika ukryty stan
 * "bieżąca sala". Lambda jest wywoływana synchronicznie, dokładnie raz.
 */
export class ScheduleBuilder {
  private readonly halls: Hall[] = [];
  private built = false;

  private constructor(private readonly date: LocalDate) {}

  static day(date: LocalDate): ScheduleBuilder {
    return new ScheduleBuilder(date);
  }

  hall(name: string, content: (hall: HallBuilder) => void): this {
    this.requireNotBuilt();
    if (this.halls.some((hall) => hall.name === name)) {
      throw new IllegalStateError(`duplicate hall: ${name}`);
    }
    const screenings: Screening[] = [];
    content(new HallBuilder(screenings));
    this.halls.push(new Hall(name, screenings));
    return this;
  }

  build(): DaySchedule {
    this.requireNotBuilt();
    this.built = true;
    return new DaySchedule(this.date, this.halls);
  }

  private requireNotBuilt(): void {
    if (this.built) {
      throw new IllegalStateError('builder already used');
    }
  }
}
