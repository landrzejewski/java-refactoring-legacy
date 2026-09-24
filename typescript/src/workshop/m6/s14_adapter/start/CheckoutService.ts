import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import { ChargeRequest, type RestPayClient, RestPayException } from '../RestPayClient.js';
import type { XmlPayGateway } from '../XmlPayGateway.js';

/**
 * Start: serwis kasy mówi dwoma językami - składa XML w groszach dla starej bramki i woła
 * API REST w złotych dla nowej. Szczegóły obu integracji wymieszane z logiką kasy.
 */
export class CheckoutService {
  constructor(private readonly xml: XmlPayGateway, private readonly rest: RestPayClient) {}

  pay(provider: string, reservationId: string, amount: Money): PaymentResult {
    if (provider === 'XML') {
      const grosze = amount.amount.times(100).toNumber();
      const request = `<charge ref='${reservationId}' amount='${grosze}'/>`;
      const response = this.xml.submit(request);
      if (response.includes("status='OK'")) {
        return PaymentResult.accepted(CheckoutService.attribute(response, 'id'));
      }
      return PaymentResult.declined(CheckoutService.attribute(response, 'code'));
    } else if (provider === 'REST') {
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
    throw new IllegalArgumentError(`unknown provider: ${provider}`);
  }

  private static attribute(xml: string, name: string): string {
    const start = xml.indexOf(`${name}='`) + name.length + 2;
    return xml.substring(start, xml.indexOf("'", start));
  }
}
