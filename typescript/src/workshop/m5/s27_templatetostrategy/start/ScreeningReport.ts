import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { ScreeningSale } from '../ScreeningSale.js';

/**
 * Start: Template Method z dwiema niezależnymi osiami w hakach - okres (które dni, jaki opis)
 * i format (CSV albo tekst). Każda kombinacja to osobna podklasa: 2 okresy x 2 formaty = 4 klasy,
 * a trzeci format dopisze kolejne dwie. Kod formatu i okresu jest skopiowany między podklasami.
 * TypeScript nie ma modyfikatora final, więc konstruktor bazy odrzuca podklasę nadpisującą render().
 */
export abstract class ScreeningReport {
  constructor() {
    if (this.render !== ScreeningReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
  }

  render(day: LocalDate, sales: readonly ScreeningSale[]): string {
    const byTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (this.includes(day, sale.day)) {
        const line = new Line(sale.tickets, sale.revenue);
        byTitle.set(sale.title, byTitle.get(sale.title)?.plus(line) ?? line);
      }
    }
    let text = this.header(this.label(day));
    let total = new Line(0, Money.ZERO);
    for (const title of sortedKeys(byTitle)) {
      const line = byTitle.get(title)!;
      text += this.row(title, line.tickets, line.revenue);
      total = total.plus(line);
    }
    return text + this.footer(total.tickets, total.revenue);
  }

  /** Hak okresu: czy sprzedaż z danego dnia należy do raportu. */
  protected abstract includes(day: LocalDate, saleDay: LocalDate): boolean;

  /** Hak okresu: opis okresu w nagłówku. */
  protected abstract label(day: LocalDate): string;

  /** Hak formatu. */
  protected abstract header(label: string): string;

  /** Hak formatu. */
  protected abstract row(title: string, tickets: number, revenue: Money): string;

  /** Hak formatu. */
  protected abstract footer(tickets: number, revenue: Money): string;
}

class Line {
  constructor(readonly tickets: number, readonly revenue: Money) {}

  plus(other: Line): Line {
    return new Line(this.tickets + other.tickets, this.revenue.plus(other.revenue));
  }
}

// Odpowiednik TreeMap<String, ...>: tytuły w kolejności jednostek UTF-16, jak String.compareTo.
function sortedKeys(map: ReadonlyMap<string, Line>): string[] {
  return [...map.keys()].sort((a, b) => (a < b ? -1 : a > b ? 1 : 0));
}
