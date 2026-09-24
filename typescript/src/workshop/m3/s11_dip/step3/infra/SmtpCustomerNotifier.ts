import { IllegalStateError } from '../../../../../shared/errors.js';
import type { CustomerNotifier } from '../app/CustomerNotifier.js';
import type { SmtpMailSender } from './SmtpMailSender.js';

/**
 * Krok 3: adapter - implementuje port polityki (import infra -> app) i tłumaczy
 * go na protokół: składa MIME, interpretuje kod SMTP, zamienia go na błąd kontraktu.
 */
export class SmtpCustomerNotifier implements CustomerNotifier {
  constructor(private readonly mail: SmtpMailSender) {}

  notifyCustomer(email: string, message: string): void {
    const mime = `To: ${email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
    const reply = this.mail.send(email, mime);
    if (!reply.startsWith('250')) {
      throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
    }
  }
}
