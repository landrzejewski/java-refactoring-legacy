import type { LocalTime } from '../../../shared/time.js';

/** Start - liść drzewa repertuaru. */
export class Screening {
  constructor(
    private readonly title: string,
    private readonly start: LocalTime,
  ) {}

  render(): string {
    return `  ${this.start.toString()} ${this.title}\n`;
  }
}
