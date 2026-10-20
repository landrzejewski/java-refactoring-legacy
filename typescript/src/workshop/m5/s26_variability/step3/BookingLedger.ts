import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Status } from '../Status.js';
import { Booking } from './Booking.js';

/**
 * Krok 3: odbiorca poleceń bez warunków po statusie - o tym, co wolno i ile pieniędzy się
 * przesuwa, decyduje stan rezerwacji (Booking). Odbiorca prowadzi tylko kasę i rejestr.
 */
export class BookingLedger {
  private readonly bookings = new Map<string, Booking>();
  private till = Money.ZERO;

  open(id: string, amount: Money): void {
    if (this.bookings.has(id)) {
      throw new IllegalArgumentError(`booking exists: ${id}`);
    }
    this.bookings.set(id, new Booking(amount));
  }

  remove(id: string): void {
    this.bookings.delete(id);
  }

  status(id: string): Status {
    return this.booking(id).status();
  }

  pay(id: string): Money {
    const paid = this.booking(id).pay();
    this.till = this.till.plus(paid);
    return paid;
  }

  use(id: string): void {
    this.booking(id).use();
  }

  cancel(id: string): Money {
    const refunded = this.booking(id).cancel();
    this.till = this.till.minus(refunded);
    return refunded;
  }

  restore(id: string, previous: Status): void {
    this.booking(id).restore(previous);
  }

  deposit(amount: Money): void {
    this.till = this.till.plus(amount);
  }

  withdraw(amount: Money): void {
    this.till = this.till.minus(amount);
  }

  summary(): string {
    const list = this.bookings.size === 0 ? '-' : [...this.bookings]
      .map(([id, booking]) => `${id}=${booking.status()}`)
      .join(', ');
    return `Kasa: ${this.till.toString()}; ${list}`;
  }

  private booking(id: string): Booking {
    const booking = this.bookings.get(id);
    if (booking === undefined) {
      throw new IllegalArgumentError(`no booking ${id}`);
    }
    return booking;
  }
}
