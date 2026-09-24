import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 1: zniżka studencka jako override - dyspozycja dynamiczna, a nie wybór metody po typie deklarowanym. */
export class StudentTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  override discountPercent(): number {
    return 25;
  }
}
