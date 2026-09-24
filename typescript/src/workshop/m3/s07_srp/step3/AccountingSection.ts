import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/** Krok 3: aktor - księgowość. Zmienia się, gdy zmieniają się stawki VAT lub wymogi sprawozdań. */
export class AccountingSection {
  render(sales: readonly Sale[]): string {
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

  private revenue(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  private net(gross: Decimal, vatPercent: number): Decimal {
    return gross.times(100)
      .dividedBy(100 + vatPercent).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
