import { Money } from '../../../shared/Money.js';
import type { Seat } from '../Seat.js';

const PRICE_2D = Money.of('25.00');
const VIP_SURCHARGE = Money.of('10.00');

/**
 * Start: publiczna, mutowalna lista miejsc. `readonly` blokuje tylko przypisanie -
 * każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.
 */
export class Booking {
  readonly seats: Seat[] = [];

  total(): Money {
    let total = Money.ZERO;
    for (const seat of this.seats) {
      total = total.plus(seat.row >= 10 ? PRICE_2D.plus(VIP_SURCHARGE) : PRICE_2D);
    }
    return total;
  }
}
