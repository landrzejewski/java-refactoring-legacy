import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/** Krok 1: te same metody co w CSV; render() przepisany na identyczny szkielet. */
export class HtmlSalesReport {
  render(sales: readonly Sale[]): string {
    const sorted = [...sales];
    sorted.sort((a, b) => a.time.compareTo(b.time));
    let text = this.header();
    let tickets = 0;
    let total = Money.ZERO;
    for (const sale of sorted) {
      text += this.row(sale);
      tickets += sale.tickets;
      total = total.plus(sale.amount);
    }
    return text + this.footer(tickets, total);
  }

  private header(): string {
    return '<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n';
  }

  private row(sale: Sale): string {
    const title = sale.title.replaceAll('&', '&amp;').replaceAll('<', '&lt;');
    return `<tr><td>${sale.time.toString()}</td><td>${title}</td><td>${sale.tickets}`
      + `</td><td>${sale.amount.toString()}</td></tr>\n`;
  }

  private footer(tickets: number, total: Money): string {
    return `<tr><td colspan="2">Suma</td><td>${tickets}</td><td>${total.toString()}</td></tr>\n`
      + '</table>\n';
  }
}
