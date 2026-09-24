import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/**
 * Start: raport dzienny obsługuje dwóch aktorów - księgowość (VAT, przychód brutto)
 * i marketing (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera
 * `revenue`. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera
 * po cichu zmieni też sumę dla księgowości.
 */
export class DailyReport {
  render(sales: readonly Sale[]): string {
    let out = '';
    // księgowość
    let tickets = new Decimal('0.00');
    let bar = new Decimal('0.00');
    for (const sale of sales) {
      tickets = tickets.plus(sale.ticketRevenue);
      bar = bar.plus(sale.barRevenue);
    }
    out += 'KSIEGOWOSC\n';
    out += `Bilety brutto ${tickets.toFixed(2)}, netto ${this.net(tickets, 8).toFixed(2)}\n`;
    out += `Bar brutto ${bar.toFixed(2)}, netto ${this.net(bar, 23).toFixed(2)}\n`;
    let total = new Decimal('0.00');
    for (const sale of sales) {
      total = total.plus(this.revenue(sale));
    }
    out += `Razem brutto ${total.toFixed(2)}\n`;
    // marketing
    out += 'MARKETING\n';
    const byTitle = new Map<string, Decimal>();
    let sold = 0;
    for (const sale of sales) {
      byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.revenue(sale)));
      sold += sale.tickets;
    }
    let hit = 'brak';
    let best = new Decimal(0);
    // tytuły w kolejności alfabetycznej - przy remisie wygrywa pierwszy
    for (const [title, value] of [...byTitle.entries()].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))) {
      if (value.comparedTo(best) > 0) {
        hit = `${title} (${value.toFixed(2)})`;
        best = value;
      }
    }
    out += `Hit dnia: ${hit}\n`;
    out += `Sprzedanych biletow: ${sold}\n`;
    return out;
  }

  private revenue(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  private net(gross: Decimal, vatPercent: number): Decimal {
    return gross.times(100)
      .dividedBy(100 + vatPercent).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
