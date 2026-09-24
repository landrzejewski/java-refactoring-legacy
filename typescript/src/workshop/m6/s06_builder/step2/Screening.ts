import type { LocalTime } from '../../../shared/time.js';

/** Krok 2: liść jako niezmienny obiekt wartości (w Javie: rekord). */
export class Screening {
  constructor(
    readonly title: string,
    readonly start: LocalTime,
  ) {}

  render(): string {
    return `  ${this.start.toString()} ${this.title}\n`;
  }
}
