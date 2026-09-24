import type { LocalDate } from '../../../shared/time.js';
import type { Hall } from './Hall.js';

/** Start - korzeń drzewa: dzień z listą sal. */
export class DaySchedule {
  private readonly halls: Hall[] = [];

  constructor(private readonly date: LocalDate) {}

  add(hall: Hall): void {
    this.halls.push(hall);
  }

  render(): string {
    let text = `${this.date.toString()} ${this.date.dayOfWeek}\n`;
    let count = 0;
    for (const hall of this.halls) {
      text += hall.render();
      count += hall.size();
    }
    return `${text}Seansow: ${count}\n`;
  }
}
