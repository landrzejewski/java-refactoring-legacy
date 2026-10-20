import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import { BookingPlan, Charge, Save, SendMail } from './BookingPlan.js';

/**
 * Krok 3: Separate Query from Modifier - czysta część nowej ścieżki. Nie ma dostępu do
 * żadnego portu efektów, więc w cieniu nie da się jej użyć "za mocno".
 */
export class BookingPlanner {
  private static readonly TICKET_2D = Money.of('25.00');
  private static readonly ONLINE_FEE = Money.of('2.00');

  plan(request: BookingRequest): BookingPlan {
    const tickets = BookingPlanner.TICKET_2D.times(request.tickets);
    const total = tickets.plus(BookingPlanner.ONLINE_FEE.times(request.tickets));
    return new BookingPlan('OK ' + total.toString(), [
      new Charge(request.card, total),
      new Save(request.title + ';' + request.email + ';'
        + request.tickets + ';' + total.toString()),
      new SendMail(request.email,
        'Bilety ' + request.title + ' x' + request.tickets + ', zaplacono ' + tickets.toString())]);
  }
}
