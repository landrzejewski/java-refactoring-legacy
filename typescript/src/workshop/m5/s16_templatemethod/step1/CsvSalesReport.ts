import { Money } from '../../../shared/Money.js';
import type { Sale } from '../Sale.js';

/**
 * Krok 1: Extract Method na różnicach (header, row, footer) - w obu raportach te same nazwy
 * i sygnatury, więc render() obu klas staje się identyczny.
 */
export class CsvSalesReport {
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
    return 'godzina;film;bilety;kwota\n';
  }

  private row(sale: Sale): string {
    const title = sale.title.includes(';') ? `"${sale.title}"` : sale.title;
    return `${sale.time.toString()};${title};${sale.tickets};${sale.amount.toString()}\n`;
  }

  private footer(tickets: number, total: Money): string {
    return `SUMA;;${tickets};${total.toString()}\n`;
  }
}
