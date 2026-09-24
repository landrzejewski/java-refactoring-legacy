import { Money } from '../../../shared/Money.js';
import type { Seat } from '../Seat.js';

const PRICE_2D = Money.of('25.00');
const VIP_SURCHARGE = Money.of('10.00');

/**
 * Krok 1: Encapsulate Collection, faza przejściowa - pole prywatne, operacje addSeat/removeSeat
 * w właścicielu, a getter zwraca TĘ SAMĄ tablicę. Zachowujemy stary alias, więc to nadal czysta
 * refaktoryzacja: klient, który jeszcze modyfikuje listę przez getter, działa jak wcześniej.
 */
export class Booking {
  readonly #seats: Seat[] = [];

  /** Przejściowo: żywa, MODYFIKOWALNA tablica - dokładnie to, co dawało publiczne pole. */
  get seats(): Seat[] {
    return this.#seats;
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
