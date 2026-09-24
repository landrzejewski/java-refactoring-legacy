import type { LoyaltyProgram } from '../LoyaltyProgram.js';
import type { Mailer } from '../Mailer.js';
import type { SmsGateway } from '../SmsGateway.js';
import { LoyaltyPoints } from './LoyaltyPoints.js';
import { MailConfirmation } from './MailConfirmation.js';
import { PaymentService } from './PaymentService.js';
import { SmsConfirmation } from './SmsConfirmation.js';

/** Krok 3: korzeń kompozycji - tu (i tylko tu) ustalamy zestaw i kolejność odbiorców. */
export class PaymentServices {
  private constructor() {}

  static standard(mailer: Mailer, sms: SmsGateway, loyalty: LoyaltyProgram): PaymentService {
    const service = new PaymentService();
    service.subscribe(new MailConfirmation(mailer));
    service.subscribe(new SmsConfirmation(sms));
    service.subscribe(new LoyaltyPoints(loyalty));
    return service;
  }
}
