import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { PaymentResult } from '../PaymentResult.js';
import type { RestPayClient } from '../RestPayClient.js';
import type { XmlPayGateway } from '../XmlPayGateway.js';
import type { PaymentGateway } from './PaymentGateway.js';
import { RestPayAdapter } from './RestPayAdapter.js';
import { XmlPayAdapter } from './XmlPayAdapter.js';

/**
 * Krok 2: Unify Interfaces with Adapter - metody przeniesione do adapterów wspólnego
 * interfejsu. Konstruktor bez zmian, wybór dostawcy jeszcze tutaj.
 */
export class CheckoutService {
  private readonly xml: PaymentGateway;
  private readonly rest: PaymentGateway;

  constructor(xml: XmlPayGateway, rest: RestPayClient) {
    this.xml = new XmlPayAdapter(xml);
    this.rest = new RestPayAdapter(rest);
  }

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    if (provider === 'XML') {
      return this.xml.pay(reservationId, amount);
    } else if (provider === 'REST') {
      return this.rest.pay(reservationId, amount);
    }
    throw new IllegalArgumentError(`unknown provider: ${provider}`);
  }
}
