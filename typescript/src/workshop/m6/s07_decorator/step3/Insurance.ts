import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/**
 * Krok 3: bez zmian - ubezpieczenie jest zawsze najbardziej zewnętrzne,
 * bo w opisie występuje na końcu.
 */
export class Insurance implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('4.00'));
  }

  description(): string {
    return `${this.inner.description()} +ubezpieczenie`;
  }
}
