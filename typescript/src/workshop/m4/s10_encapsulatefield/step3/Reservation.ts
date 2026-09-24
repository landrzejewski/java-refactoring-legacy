import { IllegalStateError } from '../../../../shared/errors.js';

/**
 * Krok 3 (ŚWIADOMA ZMIANA ZACHOWANIA, osobny commit): operacje pilnują niezmiennika.
 * Niedozwolone przejście rzuca IllegalStateError zamiast być cicho ignorowane,
 * a gość może wejść tylko na rezerwację NEW albo PAID. To już nie refaktoryzacja.
 */
export class Reservation {
  #status = 'NEW';

  get status(): string {
    return this.#status;
  }

  pay(): void {
    this.moveTo('PAID', 'NEW');
  }

  checkIn(): void {
    this.moveTo('USED', 'PAID');
  }

  cancel(): void {
    this.moveTo('CANCELLED', 'NEW', 'PAID');
  }

  expire(): void {
    this.moveTo('EXPIRED', 'NEW');
  }

  admitGuestWithoutPayment(): void {
    this.moveTo('USED', 'NEW', 'PAID');
  }

  private moveTo(target: string, ...allowedFrom: string[]): void {
    for (const allowed of allowedFrom) {
      if (this.#status === allowed) {
        this.#status = target;
        return;
      }
    }
    throw new IllegalStateError('Niedozwolone przejscie ' + this.#status + ' -> ' + target);
  }
}
