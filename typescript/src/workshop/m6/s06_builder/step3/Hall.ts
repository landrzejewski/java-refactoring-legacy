import type { Screening } from './Screening.js';

/** Krok 3: niemutowalny węzeł - lista kopiowana w konstruktorze, bez add. */
export class Hall {
  readonly screenings: readonly Screening[];

  constructor(readonly name: string, screenings: readonly Screening[]) {
    this.screenings = Object.freeze([...screenings]);
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
