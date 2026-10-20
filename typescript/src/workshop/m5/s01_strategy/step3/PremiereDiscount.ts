import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Krok 3: premiera - brak zniżek (typ biletu nie jest nawet sprawdzany, jak w start). */
export class PremiereDiscount implements DiscountPolicy {
  discount(_base: Money, _ticketType: string): Money {
    return Money.ZERO;
  }
}
