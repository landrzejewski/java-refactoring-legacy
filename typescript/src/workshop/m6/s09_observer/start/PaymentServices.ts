import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { SmsGateway } from '../SmsGateway.js';
import { PaymentService } from './PaymentService.js';

/** Start: korzeń kompozycji aplikacji - tu powstaje serwis płatności. */
export class PaymentServices {
  private constructor() {}

  static standard(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram): PaymentService {
    return new PaymentService(mailer, sms, loyalty);
  }
}
