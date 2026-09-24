import type { Money } from '../../../../shared/Money.js';
import { Quote } from './Quote.js';

/** Krok 2: spełnienie R2 - Money zamiast liczby zmiennoprzecinkowej. Moduł zgodny z ADR-0007. */
export class TicketPricing {
  private static readonly GROUP_SIZE = 10;
  private static readonly GROUP_DISCOUNT_PERCENT = 10;

  total(tickets: number, unitPrice: Money): Quote {
    let sum = unitPrice.times(tickets);
    const groupDiscount = tickets >= TicketPricing.GROUP_SIZE;
    if (groupDiscount) {
      sum = sum.minus(sum.percent(TicketPricing.GROUP_DISCOUNT_PERCENT));
    }
    return new Quote(sum, groupDiscount);
  }
}
