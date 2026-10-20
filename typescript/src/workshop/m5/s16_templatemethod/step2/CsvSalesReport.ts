import type { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';
import { SalesReport } from './SalesReport.js';

/** Krok 2: raport CSV to już tylko trzy metody formatujące. */
export class CsvSalesReport extends SalesReport {
  protected override header(): string {
    return 'godzina;film;bilety;kwota\n';
  }

  protected override row(sale: Sale): string {
    const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
    return `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
  }

  protected override footer(tickets: number, total: Money): string {
    return `SUMA;;${tickets};${total.toString()}\n`;
  }
}
