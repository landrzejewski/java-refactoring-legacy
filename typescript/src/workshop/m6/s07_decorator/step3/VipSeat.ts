import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/**
 * Krok 3: dopłata za miejsce VIP jako dekorator.
 */
export class VipSeat implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('10.00'));
  }

  description(): string {
    return `${this.inner.description()} +VIP`;
  }
}
