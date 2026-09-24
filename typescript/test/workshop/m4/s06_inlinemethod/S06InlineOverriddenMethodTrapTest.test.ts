import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';

import * as step2Online from '../../../../src/workshop/m4/s06_inlinemethod/step2/OnlineTicketPricing.js';
import * as step2 from '../../../../src/workshop/m4/s06_inlinemethod/step2/TicketPricing.js';
import type { Ticket } from '../../../../src/workshop/m4/s06_inlinemethod/Ticket.js';
import { IMAX_EVENING } from './tickets.js';

/** step2 po naiwnym Inline Method `bookingFee`: ciało z klasy bazowej wklejone do total. */
class NaivePricing {
  total(ticket: Ticket): Decimal {
    return new step2.TicketPricing().price(ticket)
      .plus(new Decimal('0.00'));
  }

  protected bookingFee(): Decimal {
    return new Decimal('0.00');
  }
}

class NaiveOnlinePricing extends NaivePricing {
  protected override bookingFee(): Decimal {
    return new Decimal('2.00');
  }
}

/**
 * Dokumentuje pułapkę: Inline Method na `bookingFee()` w klasie bazowej.
 * Podklasa nadal się kompiluje, `override` nadal jest poprawne - ale nikt już jej metody nie woła.
 */
describe('S06InlineOverriddenMethodTrapTest', () => {
  it('inliningAnOverriddenMethodSilentlyDropsTheOnlineFee', () => {
    expect(new step2Online.OnlineTicketPricing().total(IMAX_EVENING).toFixed(2)).toBe('42.00');
    // Po wklejeniu ciała bookingFee() z bazy internet sprzedaje bez opłaty.
    expect(new NaiveOnlinePricing().total(IMAX_EVENING).toFixed(2)).toBe('40.00');
  });
});
