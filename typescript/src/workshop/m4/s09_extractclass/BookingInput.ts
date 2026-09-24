import type { Money } from '../../shared/Money.js';

/**
 * Stabilne wejście testu.
 *
 * @param cards kolejne próby płatności (każda próba to jedna karta)
 */
export class BookingInput {
  constructor(
    readonly id: string,
    readonly name: string,
    readonly email: string,
    readonly phone: string,
    readonly amount: Money,
    readonly cards: readonly string[],
  ) {}
}
