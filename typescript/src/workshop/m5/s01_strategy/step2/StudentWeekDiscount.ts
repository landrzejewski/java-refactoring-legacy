import type { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/** Krok 2: tydzień studenta - student 50%, pozostali jak w programie standardowym. */
export class StudentWeekDiscount implements DiscountPolicy {
  constructor(private readonly fallback: DiscountPolicy) {}

  discount(base: Money, ticketType: string): Money {
    return ticketType === 'S' ? base.percent(50) : this.fallback.discount(base, ticketType);
  }
}
