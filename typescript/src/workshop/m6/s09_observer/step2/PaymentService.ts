import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { Payment } from '../Payment.js';
import type { SmsGateway } from '../SmsGateway.js';
import { LoyaltyPoints } from './LoyaltyPoints.js';
import { MailConfirmation } from './MailConfirmation.js';
import type { PaymentListener } from './PaymentListener.js';
import { ReservationPaid } from './ReservationPaid.js';
import { SmsConfirmation } from './SmsConfirmation.js';

/**
 * Krok 2: Replace Hard-coded Notifications with Observer - odbiorcy jako lista PaymentListener.
 * Konstruktor bez zmian, lista w starej kolejności, pętla bez try/catch (fail-fast jak w start).
 */
export class PaymentService {
  private readonly listeners: readonly PaymentListener[];
  private readonly paidList: string[] = [];

  constructor(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram) {
    this.listeners = Object.freeze([
      new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty)]);
  }

  confirm(payment: Payment): void {
    if (payment.amount.amount.lte(0)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    this.paidList.push(payment.reservationId);
    this.notifyPaid(new ReservationPaid(
      payment.reservationId, payment.email, payment.phone, payment.amount));
  }

  private notifyPaid(event: ReservationPaid): void {
    for (const listener of this.listeners) {
      listener.onPaid(event);
    }
  }

  paid(): readonly string[] {
    return Object.freeze([...this.paidList]);
  }
}
