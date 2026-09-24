import type { Money } from '../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Stabilny kontrakt sceny: bilet studencki. */
export class StudentTicket implements Ticket {
  readonly #basePrice: Money;
  readonly #studentId: string;

  constructor(basePrice: Money, studentId: string) {
    this.#basePrice = basePrice;
    this.#studentId = studentId;
  }

  basePrice(): Money {
    return this.#basePrice;
  }

  studentId(): string {
    return this.#studentId;
  }
}
