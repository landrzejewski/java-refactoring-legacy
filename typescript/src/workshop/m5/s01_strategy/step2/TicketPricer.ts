import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DiscountPolicy } from './DiscountPolicy.js';
import { PremiereDiscount } from './PremiereDiscount.js';
import { StandardDiscount } from './StandardDiscount.js';
import { StudentWeekDiscount } from './StudentWeekDiscount.js';

/**
 * Krok 2: gałęzie przeniesione do strategii (po jednej, test po każdej). W kontekście zostaje
 * wspólna walidacja i jeden switch wybierający strategię - wciąż przy każdym wywołaniu.
 */
export class TicketPricer {
  price(base: Money, ticketType: string, program: string | null): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    return base.minus(TicketPricer.policyFor(program).discount(base, ticketType));
  }

  private static policyFor(program: string): DiscountPolicy {
    switch (program) {
      case 'STANDARD': return new StandardDiscount();
      case 'STUDENT_WEEK': return new StudentWeekDiscount(new StandardDiscount());
      case 'PREMIERE': return new PremiereDiscount();
      default: throw new IllegalArgumentError(`unknown program: ${program}`);
    }
  }
}
