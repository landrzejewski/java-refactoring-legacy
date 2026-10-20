import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/**
 * Krok 2: rozdzielenie wspólnego helpera według aktora. Marketing dostaje własną
 * `popularity` - dziś liczoną tak samo jak `revenue`, ale to inna wiedza
 * z innym właścicielem. Świadome powtórzenie kodu, nie wiedzy.
 */
export class DailyReport {
  render(sales: readonly Sale[]): string {
    return this.accountingSection(sales) + this.marketingSection(sales);
  }

  private accountingSection(sales: readonly Sale[]): string {
    let out = '';
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
    return out;
  }

  private marketingSection(sales: readonly Sale[]): string {
    let out = '';
    out += 'MARKETING\n';
    const byTitle = new Map<string, Decimal>();
    let sold = 0;
    for (const sale of sales) {
      byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.popularity(sale)));
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

  /** Księgowość: przychód brutto seansu. */
  private revenue(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  /** Marketing: miara popularności filmu (dziś: bilety + bar). */
  private popularity(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  private net(gross: Decimal, vatPercent: number): Decimal {
    return gross.times(100)
      .dividedBy(100 + vatPercent).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
