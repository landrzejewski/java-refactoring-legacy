import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { Payment } from '../Payment.js';
import type { SmsGateway } from '../SmsGateway.js';

/**
 * Start: po opłaceniu serwis na sztywno woła mail, SMS i program lojalnościowy. Każdy nowy
 * odbiorca to zmiana w tej klasie. Semantyka do zachowania: kolejność i fail-fast
 * (wyjątek w SMS przerywa - punkty nie zostaną naliczone, a rezerwacja jest już opłacona).
 */
export class PaymentService {
  private readonly paidList: string[] = [];

  constructor(
    private readonly mailer: Mailer,
    private readonly sms: SmsGateway,
    private readonly loyalty: LoyaltyProgram,
  ) {}

  confirm(payment: Payment): void {
    if (payment.amount.amount.lte(0)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    this.paidList.push(payment.reservationId);
    this.mailer.send(payment.email,
      `Potwierdzenie platnosci ${payment.reservationId}: ${payment.amount.toString()}`);
    this.sms.send(payment.phone, `Oplacono ${payment.reservationId}`);
    this.loyalty.addPoints(payment.email, Math.trunc(payment.amount.amount.trunc().toNumber() / 10));
  }

  paid(): readonly string[] {
    return Object.freeze([...this.paidList]);
  }
}
