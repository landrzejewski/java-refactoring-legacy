import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Status } from '../Status.js';

/**
 * Krok 2: odbiorca poleceń - rezerwacje i kasa wydzielone z pulpitu (Extract Class).
 * Cykl życia nadal jest warunkami po statusie w każdej metodzie - to zapach na krok 3.
 * (W Javie metody są pakietowe; TypeScript nie ma tej widoczności, więc są publiczne.)
 */
export class BookingLedger {
  private readonly statuses = new Map<string, Status>();
  private readonly amounts = new Map<string, Money>();
  private till = Money.ZERO;

  open(id: string, amount: Money): void {
    if (this.statuses.has(id)) {
      throw new IllegalArgumentError(`booking exists: ${id}`);
    }
    this.statuses.set(id, Status.NEW);
    this.amounts.set(id, amount);
  }

  remove(id: string): void {
    this.statuses.delete(id);
    this.amounts.delete(id);
  }

  status(id: string): Status {
    const status = this.statuses.get(id);
    if (status === undefined) {
      throw new IllegalArgumentError(`no booking ${id}`);
    }
    return status;
  }

  pay(id: string): Money {
    const status = this.status(id);
    if (status !== Status.NEW) {
      throw BookingLedger.invalid('pay', status);
    }
    const amount = this.amountOf(id);
    this.till = this.till.plus(amount);
    this.statuses.set(id, Status.PAID);
    return amount;
  }

  use(id: string): void {
    const status = this.status(id);
    if (status !== Status.PAID) {
      throw BookingLedger.invalid('use', status);
    }
    this.statuses.set(id, Status.USED);
  }

  cancel(id: string): Money {
    const status = this.status(id);
    if (status === Status.NEW) {
      this.statuses.set(id, Status.CANCELLED);
      return Money.ZERO;
    } else if (status === Status.PAID) {
      const amount = this.amountOf(id);
      this.till = this.till.minus(amount);
      this.statuses.set(id, Status.CANCELLED);
      return amount;
    }
    throw BookingLedger.invalid('cancel', status);
  }

  restore(id: string, previous: Status): void {
    this.statuses.set(id, previous);
  }

  deposit(amount: Money): void {
    this.till = this.till.plus(amount);
  }

  withdraw(amount: Money): void {
    this.till = this.till.minus(amount);
  }

  summary(): string {
    const bookings = this.statuses.size === 0 ? '-' : [...this.statuses]
      .map(([id, status]) => `${id}=${status}`)
      .join(', ');
    return `Kasa: ${this.till.toString()}; ${bookings}`;
  }

  private amountOf(id: string): Money {
    return this.amounts.get(id) ?? Money.ZERO;
  }

  private static invalid(action: string, status: Status): IllegalStateError {
    return new IllegalStateError(`cannot ${action} in ${status}`);
  }
}
