import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Effects } from './Effects.js';

/**
 * Krok 1: Parameterize Constructor - efekty przez port Effects zamiast Infrastructure.
 * Refaktoryzacja przygotowawcza: z prawdziwym adapterem zachowanie (także błędne) jest takie samo.
 */
export class NewBookingFlow {
  private static readonly TICKET_2D = Money.of('25.00');
  private static readonly ONLINE_FEE = Money.of('2.00');

  constructor(private readonly effects: Effects) {}

  book(request: BookingRequest): string {
    const tickets = NewBookingFlow.TICKET_2D.times(request.tickets);
    const total = tickets.plus(NewBookingFlow.ONLINE_FEE.times(request.tickets));
    this.effects.charge(request.card, total);
    this.effects.save(request.title + ';' + request.email + ';' + request.tickets + ';' + total.toString());
    this.effects.sendMail(request.email,
      'Bilety ' + request.title + ' x' + request.tickets + ', zaplacono ' + tickets.toString());
    return 'OK ' + total.toString();
  }
}
