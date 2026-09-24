import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import type { PaymentResult } from '../../../../src/workshop/m6/s14_adapter/PaymentResult.js';
import { RestPayClient } from '../../../../src/workshop/m6/s14_adapter/RestPayClient.js';
import * as start from '../../../../src/workshop/m6/s14_adapter/start/CheckoutService.js';
import * as step1 from '../../../../src/workshop/m6/s14_adapter/step1/CheckoutService.js';
import * as step2 from '../../../../src/workshop/m6/s14_adapter/step2/CheckoutService.js';
import * as step3 from '../../../../src/workshop/m6/s14_adapter/step3/CheckoutService.js';
import { XmlPayGateway } from '../../../../src/workshop/m6/s14_adapter/XmlPayGateway.js';
import { Scene } from '../../support/scene.js';

interface Checkout {
  pay(provider: string, reservationId: string, amount: Money): PaymentResult;
}

function safe(checkout: Checkout): (input: readonly string[]) => string {
  return ([provider = '', reservationId = '', amount = '']) => {
    try {
      const result = checkout.pay(provider, reservationId, Money.of(amount));
      return result.accepted ? `OK ${result.transactionId}` : `DECLINED ${result.declineCode}`;
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}

/** Wejście: [dostawca, rezerwacja, kwota]. Obie bramki, sukces i odmowa, nieznany dostawca. */
describe('S14EquivalenceTest', () => {
  describe('everyStepPaysTheSameWay', () => {
    const xml = new XmlPayGateway();
    const rest = new RestPayClient();
    Scene.variants<readonly string[], string>()
      .variant('start', safe(new start.CheckoutService(xml, rest)))
      .variant('step1', safe(new step1.CheckoutService(xml, rest)))
      .variant('step2', safe(new step2.CheckoutService(xml, rest)))
      .variant('step3', safe(new step3.CheckoutService(xml, rest)))
      .expect('XML sukces', ['XML', 'R1', '40.00'], 'OK X-R1')
      .expect('XML grosze', ['XML', 'R2', '0.50'], 'OK X-R2')
      .expect('XML odmowa', ['XML', 'R3', '600.00'], 'DECLINED 51')
      .expect('REST sukces', ['REST', 'R4', '40.00'], 'OK T-R4')
      .expect('REST odmowa jako wyjątek biblioteki', ['REST', 'R5', '600.00'], 'DECLINED LIMIT')
      .expect('granica 500.00 włącznie', ['REST', 'R6', '500.00'], 'OK T-R6')
      .expect('nieznany dostawca', ['SWIFT', 'R7', '40.00'], 'ERROR unknown provider: SWIFT')
      .tests();
  });
});
