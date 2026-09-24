import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/**
 * Krok 3: okulary 3D jako dekorator; decyzja "czy potrzebne" została w fabryce.
 */
export class Glasses3D implements PricedTicket {
  constructor(readonly inner: PricedTicket) {}

  price(): Money {
    return this.inner.price().plus(Money.of('3.00'));
  }

  description(): string {
    return `${this.inner.description()} +okulary 3D`;
  }
}
