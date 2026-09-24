import { UnsupportedOperationError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Seat } from '../Seat.js';

const PRICE_2D = Money.of('25.00');
const VIP_SURCHARGE = Money.of('10.00');

/**
 * Krok 2 (zmiana kontraktu): getter zwraca niemodyfikowalny WIDOK (odpowiednik
 * Collections.unmodifiableList). Klient nie zmieni członkostwa (typ `readonly Seat[]` w kompilacji,
 * UnsupportedOperationError w czasie wykonania), ale widzi późniejsze zmiany właściciela.
 * Bezpieczne dopiero, gdy żaden klient nie modyfikuje listy przez getter.
 */
export class Booking {
  readonly #seats: Seat[] = [];

  /** Żywy widok tylko do odczytu. */
  get seats(): readonly Seat[] {
    return new Proxy(this.#seats, READ_ONLY);
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

// Pułapki Proxy: każda próba zapisu (push, splice, przypisanie indeksu) kończy się wyjątkiem.
const READ_ONLY: ProxyHandler<Seat[]> = {
  set: () => {
    throw new UnsupportedOperationError('lista miejsc tylko do odczytu');
  },
  deleteProperty: () => {
    throw new UnsupportedOperationError('lista miejsc tylko do odczytu');
  },
};
