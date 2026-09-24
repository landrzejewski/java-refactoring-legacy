import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { PaymentResult } from '../../../../src/workshop/m6/s14_adapter/PaymentResult.js';
import { CheckoutService } from '../../../../src/workshop/m6/s14_adapter/step3/CheckoutService.js';
import type { PaymentGateway } from '../../../../src/workshop/m6/s14_adapter/step3/PaymentGateway.js';
import { XmlPayAdapter } from '../../../../src/workshop/m6/s14_adapter/step3/XmlPayAdapter.js';
import { XmlPayGateway } from '../../../../src/workshop/m6/s14_adapter/XmlPayGateway.js';

/** Adapter tłumaczy jednostki; serwis da się testować bez żadnej bramki. */
describe('S14SolutionTest', () => {
  it('xmlAdapterSendsAmountInGrosze', () => {
    const sent: string[] = [];
    const recording = new (class extends XmlPayGateway {
      override submit(xml: string): string {
        sent.push(xml);
        return super.submit(xml);
      }
    })();
    new XmlPayAdapter(recording).pay('R1', Money.of('40.00'));
    expect(sent).toEqual(["<charge ref='R1' amount='4000'/>"]);
  });

  it('checkoutWorksWithAnyGateway', () => {
    const fake: PaymentGateway = { pay: (reservationId) => PaymentResult.accepted(`FAKE-${reservationId}`) };
    expect(new CheckoutService(new Map([['FAKE', fake]])).pay('FAKE', 'R9', Money.of('25.00')).transactionId)
      .toBe('FAKE-R9');
  });
});
