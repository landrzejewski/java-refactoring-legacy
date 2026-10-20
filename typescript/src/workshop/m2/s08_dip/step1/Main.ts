import { ConfirmReservation } from './app/ConfirmReservation.js';
import { SmtpMailSender } from './infra/SmtpMailSender.js';

/** Composition root wariantu: jedyne miejsce, które składa graf obiektów. */
export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation(new SmtpMailSender('smtp.kino.pl', 25));
}
