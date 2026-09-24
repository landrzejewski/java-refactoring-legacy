import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Start: raport CSV - szkielet (sortowanie, nagłówek, wiersze, suma) skopiowany w raporcie HTML. */
export class CsvSalesReport {
  render(sales: readonly Sale[]): string {
    const sorted = [...sales];
    sorted.sort((a, b) => a.time.compareTo(b.time));
    let csv = 'godzina;film;bilety;kwota\n';
    let tickets = 0;
    let total = Money.ZERO;
    for (const sale of sorted) {
      const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
      csv += `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
      tickets += sale.tickets;
      total = total.plus(sale.amount);
    }
    csv += `SUMA;;${tickets};${total.toString()}\n`;
    return csv;
  }
}
