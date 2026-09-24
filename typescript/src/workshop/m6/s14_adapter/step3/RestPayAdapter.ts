import type { Money } from '../../../shared/Money.js';
import { PaymentResult } from '../PaymentResult.js';
import { ChargeRequest, type RestPayClient, RestPayException } from '../RestPayClient.js';
import type { PaymentGateway } from './PaymentGateway.js';

/** Krok 3: adapter nowej bramki - odmowa zgłaszana wyjątkiem staje się wynikiem declined. */
export class RestPayAdapter implements PaymentGateway {
  constructor(private readonly rest: RestPayClient) {}

  pay(reservationId: string, amount: Money): PaymentResult {
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
}
