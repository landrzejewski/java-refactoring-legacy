import { Money } from '../../../shared/Money.js';
import type { BookingLedger } from '../BookingLedger.js';

class TitleSales {
  static readonly NONE = new TitleSales(0, Money.ZERO);

  constructor(readonly tickets: number, readonly value: Money) {}

  plus(other: TitleSales): TitleSales {
    return new TitleSales(this.tickets + other.tickets, this.value.plus(other.value));
  }
}

/** Krok 3: nowy moduł raportów - ten sam format wyjścia, sumy w Money zamiast double. */
export class ReportModule {
  constructor(private readonly ledger: BookingLedger) {}

  report(): string {
    const byTitle = new Map<string, TitleSales>();
    for (const booking of this.ledger.all()) {
      const sales = new TitleSales(booking.tickets, booking.ticketsValue);
      byTitle.set(booking.title, byTitle.get(booking.title)?.plus(sales) ?? sales);
    }
    const sorted = [...byTitle.entries()].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0));
    const total = sorted.reduce((sum, [, sales]) => sum.plus(sales), TitleSales.NONE);
    const fees = this.ledger.all().map((booking) => booking.fees).reduce((sum, fee) => sum.plus(fee), Money.ZERO);
    let text = 'RAPORT\n';
    for (const [title, sales] of sorted) {
      text += `${title}: ${sales.tickets} bil., ${sales.value.toString()}\n`;
    }
    return text
      + `Biletow: ${total.tickets}\n`
      + `Przychod z biletow: ${total.value.toString()}\n`
      + `Oplaty rezerwacyjne: ${fees.toString()}\n`;
  }
}
