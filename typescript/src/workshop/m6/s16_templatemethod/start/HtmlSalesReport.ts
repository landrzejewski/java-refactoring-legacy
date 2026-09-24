import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Start: raport HTML - ten sam szkielet co CSV, napisany trochę inaczej. */
export class HtmlSalesReport {
  render(sales: readonly Sale[]): string {
    const html: string[] = [];
    html.push('<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n');
    let sum = Money.ZERO;
    let count = 0;
    for (const sale of sales.toSorted((a, b) => a.time.compareTo(b.time))) {
      const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
      html.push('<tr><td>', sale.time.toString(), '</td><td>', title,
        '</td><td>', String(sale.tickets), '</td><td>', sale.amount.toString(),
        '</td></tr>\n');
      sum = sum.plus(sale.amount);
      count += sale.tickets;
    }
    html.push('<tr><td colspan="2">Suma</td><td>', String(count), '</td><td>', sum.toString(),
      '</td></tr>\n</table>\n');
    return html.join('');
  }
}
