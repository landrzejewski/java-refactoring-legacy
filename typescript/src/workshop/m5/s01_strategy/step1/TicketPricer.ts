import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';

/**
 * Krok 1: Extract Interface + strategia przejściowa. Kontekst woła już DiscountPolicy,
 * ale jedyna implementacja to lambda delegująca do starego łańcucha if. Mały, odwracalny ruch.
 */
export class TicketPricer {
  price(base: Money, ticketType: string, program: string | null): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    const policy: DiscountPolicy = { discount: (b, type) => TicketPricer.legacyDiscount(b, type, program) };
    return base.minus(policy.discount(base, ticketType));
  }

  private static legacyDiscount(base: Money, ticketType: string, program: string): Money {
    if (program === 'PREMIERE') {
      return Money.ZERO;
    } else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
      return base.percent(50);
    } else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
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
    throw new IllegalArgumentError(`unknown program: ${program}`);
  }
}
