import { IllegalStateError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { ScreeningSale } from '../ScreeningSale.js';
import type { ReportFormat } from './ReportFormat.js';

/**
 * Krok 1: Replace Inheritance with Delegation dla osi "format". Szkielet deleguje formatowanie
 * do ReportFormat podanego w konstruktorze; w hierarchii zostały tylko haki okresu.
 * TypeScript nie ma modyfikatora final, więc konstruktor bazy odrzuca podklasę nadpisującą render().
 */
export abstract class ScreeningReport {
  private readonly format: ReportFormat;

  protected constructor(format: ReportFormat) {
    if (this.render !== ScreeningReport.prototype.render) {
      throw new IllegalStateError('render() is final');
    }
    this.format = requireNonNull(format, 'format');
  }

  render(day: LocalDate, sales: readonly ScreeningSale[]): string {
    const byTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (this.includes(day, sale.day)) {
        const line = new Line(sale.tickets, sale.revenue);
        byTitle.set(sale.title, byTitle.get(sale.title)?.plus(line) ?? line);
      }
    }
    let text = this.format.header(this.label(day));
    let total = new Line(0, Money.ZERO);
    for (const title of sortedKeys(byTitle)) {
      const line = byTitle.get(title)!;
      text += this.format.row(title, line.tickets, line.revenue);
      total = total.plus(line);
    }
    return text + this.format.footer(total.tickets, total.revenue);
  }

  /** Hak okresu: czy sprzedaż z danego dnia należy do raportu. */
  protected abstract includes(day: LocalDate, saleDay: LocalDate): boolean;

  /** Hak okresu: opis okresu w nagłówku. */
  protected abstract label(day: LocalDate): string;
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
