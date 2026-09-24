import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 1: bez zmian - ta wersja label() jest wzorcem dla pozostałych. */
export class StandardTicket extends Ticket {
  constructor(title: string, basePrice: Money) {
    super(title, basePrice);
  }

  price(): Money {
    return this.basePrice();
  }

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
