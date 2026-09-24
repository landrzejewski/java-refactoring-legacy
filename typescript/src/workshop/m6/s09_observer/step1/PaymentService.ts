import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { Payment } from '../Payment.js';
import type { SmsGateway } from '../SmsGateway.js';
import { ReservationPaid } from './ReservationPaid.js';

/** Krok 1: Extract Method notifyPaid + obiekt zdarzenia. Powiadomienia nadal na sztywno. */
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
    this.notifyPaid(new ReservationPaid(
      payment.reservationId, payment.email, payment.phone, payment.amount));
  }

  private notifyPaid(event: ReservationPaid): void {
    this.mailer.send(event.email,
      `Potwierdzenie platnosci ${event.reservationId}: ${event.amount.toString()}`);
    this.sms.send(event.phone, `Oplacono ${event.reservationId}`);
    this.loyalty.addPoints(event.email, Math.trunc(event.amount.amount.trunc().toNumber() / 10));
  }

  paid(): readonly string[] {
    return Object.freeze([...this.paidList]);
  }
}
