import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Krok 2: gałąź STANDARD przeniesiona do strategii. Bezstanowa - można ją współdzielić. */
export class StandardDiscount implements DiscountPolicy {
  discount(base: Money, ticketType: string): Money {
    let percent: number;
    switch (ticketType) {
      case 'N': percent = 0; break;
      case 'S': percent = 25; break;
      case 'E': percent = 30; break;
      case 'C': percent = 40; break;
      default: throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
    }
    return base.percent(percent);
  }
}
