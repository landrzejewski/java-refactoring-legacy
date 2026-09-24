import { Money } from '../../../shared/Money.js';
import type { PricedTicket } from './PricedTicket.js';

/**
 * Krok 2: pierwszy dodatek jako dekorator. Zaczynamy od ubezpieczenia, bo w opisie jest
 * ostatnie - dekorator dopisuje się na końcu opisu obiektu, który owija.
 * (W Javie: rekord - niezmienny obiekt wartości.)
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
