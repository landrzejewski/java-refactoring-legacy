import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketPricing } from './TicketPricing.js';

function discounted(percent: number): TicketPricing {
  return { price: (base) => base.minus(base.percent(percent)) };
}

/**
 * Krok 1: jedno miejsce wyboru strategii po kodzie typu biletu (N, S, E, C).
 * Strategie są bezstanowe, więc współdzielimy jedne instancje.
 */
export class TicketPricings {
  private static readonly BY_TYPE: ReadonlyMap<string, TicketPricing> = new Map<string, TicketPricing>([
    ['N', { price: (base: Money) => base }],
    ['S', discounted(25)],
    ['E', discounted(30)],
    ['C', discounted(40)],
  ]);

  private constructor() {}

  static forType(code: string): TicketPricing {
    const pricing = TicketPricings.BY_TYPE.get(code);
    if (pricing === undefined) {
      throw new IllegalArgumentError(`unknown ticket type: ${code}`);
    }
    return pricing;
  }
}
