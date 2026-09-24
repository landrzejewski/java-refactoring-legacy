import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/** Krok 3: aktor - marketing. Zmienia się, gdy marketing zmienia definicję "hitu". */
export class MarketingSection {
  render(sales: readonly Sale[]): string {
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

  private popularity(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }
}
