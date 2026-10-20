import { ConfirmReservation } from './app/ConfirmReservation.js';
import { SmtpCustomerNotifier } from './infra/SmtpCustomerNotifier.js';
import { SmtpMailSender } from './infra/SmtpMailSender.js';

/** Composition root wariantu: jedyne miejsce, które zna adapter i składa graf obiektów. */
export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender('smtp.kino.pl', 25)));
}
