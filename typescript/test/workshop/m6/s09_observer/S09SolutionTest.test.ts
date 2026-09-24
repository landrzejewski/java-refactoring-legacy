import { describe, expect, it } from 'vitest';

import { Payment } from '../../../../src/workshop/m6/s09_observer/Payment.js';
import type { PaymentListener } from '../../../../src/workshop/m6/s09_observer/step3/PaymentListener.js';
import { PaymentService } from '../../../../src/workshop/m6/s09_observer/step3/PaymentService.js';
import type { Subscription } from '../../../../src/workshop/m6/s09_observer/step3/Subscription.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/** Kontrakt subjectu po refaktoryzacji: rejestracje, wyrejestrowanie, rozszerzenie. */
describe('S09SolutionTest', () => {
  const payment = new Payment('R7', 'jan@kino.pl', '600', Money.of('25.00'));

  it('sameListenerSubscribedTwiceIsNotifiedTwice', () => {
    const log: string[] = [];
    const listener: PaymentListener = { onPaid: (event) => log.push(event.reservationId) };
    const service = new PaymentService();
    service.subscribe(listener);
    service.subscribe(listener);
    service.confirm(payment);
    expect(log).toEqual(['R7', 'R7']);
  });

  it('closingSubscriptionIsIdempotentAndRemovesOnlyItsOwnRegistration', () => {
    const log: string[] = [];
    const listener: PaymentListener = { onPaid: (event) => log.push(event.reservationId) };
    const service = new PaymentService();
    const first: Subscription = service.subscribe(listener);
    service.subscribe(listener);
    first.close();
    first.close();
    service.confirm(payment);
    expect(log).toEqual(['R7']);
  });

  it('newReceiverIsAnExtensionNotARefactoring', () => {
    const log: string[] = [];
    const service = new PaymentService();
    service.subscribe({ onPaid: (event) => log.push(`push ${event.reservationId}`) });
    service.confirm(payment);
    expect(log).toEqual(['push R7']);
  });
});
