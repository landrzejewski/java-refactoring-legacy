import type { Money } from '../../shared/Money.js';

/** Wiersz bazy: wartość biletów i opłaty rezerwacyjne osobno. */
export class Booking {
  constructor(
    readonly id: string,
    readonly email: string,
    readonly title: string,
    readonly tickets: number,
    readonly ticketsValue: Money,
    readonly fees: Money,
  ) {}
}

/**
 * Wspólna baza rezerwacji. W trakcie duszenia stary i nowy kod korzystają z tych samych danych,
 * dlatego raport legacy widzi rezerwacje przyjęte już przez nowy moduł.
 */
export class BookingLedger {
  private readonly bookings: Booking[] = [];
  private sequence = 1;

  nextId(): string {
    return 'B' + this.sequence++;
  }

  add(booking: Booking): void {
    this.bookings.push(booking);
  }

  all(): readonly Booking[] {
    return Object.freeze([...this.bookings]);
  }
}
