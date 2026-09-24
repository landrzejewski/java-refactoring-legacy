import type { LocalDate } from '../../../shared/time.js';
import type { Hall } from './Hall.js';

/** Krok 3: niemutowalny korzeń - builder jest jedyną wygodną drogą budowy. */
export class DaySchedule {
  readonly halls: readonly Hall[];

  constructor(readonly date: LocalDate, halls: readonly Hall[]) {
    this.halls = Object.freeze([...halls]);
  }

  render(): string {
    let text = `${this.date.toString()} ${this.date.dayOfWeek}\n`;
    let count = 0;
    for (const hall of this.halls) {
      text += hall.render();
      count += hall.screenings.length;
    }
    return `${text}Seansow: ${count}\n`;
  }
}
