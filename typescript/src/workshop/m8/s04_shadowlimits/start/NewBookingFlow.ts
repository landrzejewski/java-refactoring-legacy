import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';

/**
 * Start: nowa ścieżka rezerwacji. Liczy poprawnie, ale obliczenie jest splecione z efektami:
 * sama obciąża kartę, zapisuje wiersz i wysyła mail.
 */
export class NewBookingFlow {
  private static readonly TICKET_2D = Money.of('25.00');
  private static readonly ONLINE_FEE = Money.of('2.00');

  constructor(private readonly infra: Infrastructure) {}

  book(request: BookingRequest): string {
    const tickets = NewBookingFlow.TICKET_2D.times(request.tickets);
    const total = tickets.plus(NewBookingFlow.ONLINE_FEE.times(request.tickets));
    this.infra.charge(request.card, total);
    this.infra.save(request.title + ';' + request.email + ';' + request.tickets + ';' + total.toString());
    this.infra.sendMail(request.email,
      'Bilety ' + request.title + ' x' + request.tickets + ', zaplacono ' + tickets.toString());
    return 'OK ' + total.toString();
  }
}
