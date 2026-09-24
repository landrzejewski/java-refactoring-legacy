import type { Money } from '../../shared/Money.js';

/**
 * "Świat zewnętrzny" sceny: poczta, bramka płatności i baza. Każde wywołanie to prawdziwy,
 * nieodwracalny efekt - dlatego zapisujemy je w dzienniku, który sprawdzają testy.
 */
export class Infrastructure {
  private readonly entries: string[] = [];

  sendMail(to: string, text: string): void {
    this.entries.push(`MAIL ${to}: ${text}`);
  }

  charge(card: string, amount: Money): void {
    this.entries.push(`CHARGE ${card}: ${amount.toString()}`);
  }

  save(row: string): void {
    this.entries.push(`SAVE ${row}`);
  }

  log(): readonly string[] {
    return Object.freeze([...this.entries]);
  }

  count(kind: string): number {
    return this.entries.filter((entry) => entry.startsWith(kind + ' ')).length;
  }
}
