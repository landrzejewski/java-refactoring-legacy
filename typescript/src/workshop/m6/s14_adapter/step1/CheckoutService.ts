import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import { ChargeRequest, type RestPayClient, RestPayException } from '../RestPayClient.js';
import type { XmlPayGateway } from '../XmlPayGateway.js';

/** Krok 1: Extract Method - dwie gałęzie jako metody o IDENTYCZNEJ sygnaturze i wyniku. */
export class CheckoutService {
  constructor(private readonly xml: XmlPayGateway, private readonly rest: RestPayClient) {}

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    if (provider === 'XML') {
      return this.payWithXml(reservationId, amount);
    } else if (provider === 'REST') {
      return this.payWithRest(reservationId, amount);
    }
    throw new IllegalArgumentError(`unknown provider: ${provider}`);
  }

  private payWithXml(reservationId: string, amount: Money): PaymentResult {
    const grosze = amount.amount.times(100).toNumber();
    const response = this.xml.submit(`<charge ref='${reservationId}' amount='${grosze}'/>`);
    if (response.includes("status='OK'")) {
      return PaymentResult.accepted(CheckoutService.attribute(response, 'id'));
    }
    return PaymentResult.declined(CheckoutService.attribute(response, 'code'));
  }

  private payWithRest(reservationId: string, amount: Money): PaymentResult {
    try {
      return PaymentResult.accepted(
        this.rest.charge(new ChargeRequest(amount.amount, 'PLN', reservationId)).transactionId);
    } catch (exception) {
      if (exception instanceof RestPayException) {
        return PaymentResult.declined(exception.code);
      }
      throw exception;
    }
  }

  private static attribute(xml: string, name: string): string {
    const start = xml.indexOf(`${name}='`) + name.length + 2;
    return xml.substring(start, xml.indexOf("'", start));
  }
}
