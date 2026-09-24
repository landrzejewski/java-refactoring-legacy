import { Money } from '../../../shared/Money.js';
import { Booking, type BookingLedger } from '../BookingLedger.js';

/** Krok 3 (bez zmian): nowy moduł rezerwacji - Money, nazwane reguły, wspólna baza z legacy. */
export class BookingModule {
  private static readonly ONLINE_FEE = Money.of('2.00');
  private static readonly GROUP_SIZE = 10;

  constructor(private readonly ledger: BookingLedger) {}

  book(email: string, title: string, format: number, tickets: number, web: boolean): string {
    if (tickets <= 0) {
      return 'ERROR: no seats';
    }
    let value = BookingModule.basePrice(format).times(tickets);
    if (tickets >= BookingModule.GROUP_SIZE) {
      value = value.minus(value.percent(10));
    }
    const fees = web ? BookingModule.ONLINE_FEE.times(tickets) : Money.ZERO;
    const id = this.ledger.nextId();
    this.ledger.add(new Booking(id, email, title, tickets, value, fees));
    return id;
  }

  private static basePrice(format: number): Money {
    switch (format) {
      case 1: return Money.of('25.00');
      case 2: return Money.of('32.00');
      default: return Money.of('40.00');
    }
  }
}
