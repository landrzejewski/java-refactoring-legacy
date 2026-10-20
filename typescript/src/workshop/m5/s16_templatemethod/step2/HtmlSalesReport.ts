import type { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';
import { SalesReport } from './SalesReport.js';

/** Krok 2: raport HTML to już tylko trzy metody formatujące. */
export class HtmlSalesReport extends SalesReport {
  protected override header(): string {
    return '<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n';
  }

  protected override row(sale: Sale): string {
    const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
    return `<tr><td>${sale.time.toString()}</td><td>${title}</td><td>${sale.tickets}`
      + `</td><td>${sale.amount.toString()}</td></tr>\n`;
  }

  protected override footer(tickets: number, total: Money): string {
    return `<tr><td colspan="2">Suma</td><td>${tickets}</td><td>${total.toString()}</td></tr>\n`
      + '</table>\n';
  }
}
