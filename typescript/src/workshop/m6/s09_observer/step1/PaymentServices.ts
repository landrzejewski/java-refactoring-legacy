import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { SmsGateway } from '../SmsGateway.js';
import { PaymentService } from './PaymentService.js';

/** Krok 1: bez zmian - korzeń kompozycji. */
export class PaymentServices {
  private constructor() {}

  static standard(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram): PaymentService {
    return new PaymentService(mailer, sms, loyalty);
  }
}
