import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { DiscountPolicy } from './DiscountPolicy.js';
import { PremiereDiscount } from './PremiereDiscount.js';
import { StandardDiscount } from './StandardDiscount.js';
import { StudentWeekDiscount } from './StudentWeekDiscount.js';

/**
 * Krok 3: wybór strategii przeniesiony do korzenia kompozycji (konfiguracja kina).
 * Strategie są bezstanowe, więc współdzielimy jedne instancje.
 */
export class DiscountPrograms {
  private static readonly STANDARD: DiscountPolicy = new StandardDiscount();
  private static readonly STUDENT_WEEK: DiscountPolicy = new StudentWeekDiscount(DiscountPrograms.STANDARD);
  private static readonly PREMIERE: DiscountPolicy = new PremiereDiscount();

  private constructor() {}

  static forName(program: string | null): DiscountPolicy {
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    switch (program) {
      case 'STANDARD': return DiscountPrograms.STANDARD;
      case 'STUDENT_WEEK': return DiscountPrograms.STUDENT_WEEK;
      case 'PREMIERE': return DiscountPrograms.PREMIERE;
      default: throw new IllegalArgumentError(`unknown program: ${program}`);
    }
  }
}
