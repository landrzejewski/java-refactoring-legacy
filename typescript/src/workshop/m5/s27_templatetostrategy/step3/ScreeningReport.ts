import { IllegalStateError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import type { ScreeningSale } from '../ScreeningSale.js';
import type { ReportFormat } from './ReportFormat.js';
import type { ReportPeriod } from './ReportPeriod.js';

/**
 * Krok 3: jedna finalna klasa złożona z dwóch strategii (Inline Class dla czterech podklas).
 * Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach
 * dziedziczenie mnoży klasy (2 x 2), a kompozycja je dodaje (2 + 2) i pozwala wybrać
 * kombinację w czasie działania. Kolejność kroków raportu nadal pilnuje jedna metoda render.
 * TypeScript nie ma modyfikatora final, więc konstruktor odrzuca każdą podklasę (new.target).
 */
export class ScreeningReport {
  private readonly period: ReportPeriod;
  private readonly format: ReportFormat;

  constructor(period: ReportPeriod, format: ReportFormat) {
    if (new.target !== ScreeningReport) {
      throw new IllegalStateError('ScreeningReport is final');
    }
    this.period = requireNonNull(period, 'period');
    this.format = requireNonNull(format, 'format');
  }

  render(day: LocalDate, sales: readonly ScreeningSale[]): string {
    const byTitle = new Map<string, Line>();
    for (const sale of sales) {
      if (this.period.includes(day, sale.day)) {
        const line = new Line(sale.tickets, sale.revenue);
        byTitle.set(sale.title, byTitle.get(sale.title)?.plus(line) ?? line);
      }
    }
    let text = this.format.header(this.period.label(day));
    let total = new Line(0, Money.ZERO);
    for (const title of sortedKeys(byTitle)) {
      const line = byTitle.get(title)!;
      text += this.format.row(title, line.tickets, line.revenue);
      total = total.plus(line);
    }
    return text + this.format.footer(total.tickets, total.revenue);
  }
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
