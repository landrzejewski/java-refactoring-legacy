import type { Screening } from './Screening.js';

/** Start - mutowalny węzeł: sala z listą seansów. */
export class Hall {
  private readonly screenings: Screening[] = [];

  constructor(private readonly name: string) {}

  add(screening: Screening): void {
    this.screenings.push(screening);
  }

  size(): number {
    return this.screenings.length;
  }

  render(): string {
    let text = `${this.name}\n`;
    if (this.screenings.length === 0) {
      text += '  (brak seansow)\n';
    }
    this.screenings.forEach((screening) => {
      text += screening.render();
    });
    return text;
  }
}
