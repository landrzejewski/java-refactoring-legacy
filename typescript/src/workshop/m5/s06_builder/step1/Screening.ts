import type { LocalTime } from '../../../shared/time.js';

/** Krok 1: bez zmian - liść drzewa repertuaru. */
export class Screening {
  constructor(
    private readonly title: string,
    private readonly start: LocalTime,
  ) {}

  render(): string {
    return `  ${this.start.toString()} ${this.title}\n`;
  }
}
