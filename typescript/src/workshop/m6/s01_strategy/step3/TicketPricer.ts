import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/**
 * Krok 3: kontekst dostaje strategię w konstruktorze i nie zna nazw programów.
 * Uwaga: wybór w konstruktorze zamraża decyzję - zmienia moment, w którym pada błąd nieznanego programu.
 */
export class TicketPricer {
  private readonly policy: DiscountPolicy;

  constructor(policy: DiscountPolicy) {
    this.policy = requireNonNull(policy, 'policy');
  }

  price(base: Money, ticketType: string): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    return base.minus(this.policy.discount(base, ticketType));
  }
}
