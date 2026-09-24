import { Money } from '../../../shared/Money.js';
import type { Seat } from '../Seat.js';

const PRICE_2D = Money.of('25.00');
const VIP_SURCHARGE = Money.of('10.00');

/**
 * Krok 3 (zmiana kontraktu): getter zwraca MIGAWKĘ (`Object.freeze([...])`, odpowiednik List.copyOf).
 * Klient nie zmieni członkostwa i NIE widzi późniejszych zmian - dostaje stan z chwili wywołania.
 * Wybór między widokiem a migawką to decyzja o kontrakcie, nie szczegół implementacji.
 */
export class Booking {
  readonly #seats: Seat[] = [];

  /** Niemodyfikowalna kopia z chwili wywołania. */
  get seats(): readonly Seat[] {
    return Object.freeze([...this.#seats]);
  }

  addSeat(seat: Seat): void {
    this.#seats.push(seat);
  }

  removeSeat(seat: Seat): void {
    const index = this.#seats.findIndex((s) => s.equals(seat));
    if (index >= 0) {
      this.#seats.splice(index, 1);
    }
  }

  total(): Money {
    let total = Money.ZERO;
    for (const seat of this.#seats) {
      total = total.plus(seat.row >= 10 ? PRICE_2D.plus(VIP_SURCHARGE) : PRICE_2D);
    }
    return total;
  }
}
