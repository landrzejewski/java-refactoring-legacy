import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 2 (rozwiązanie): Move Method - strażnicy przeniesieni do konstruktora.
 * Inwarianty mają jednego właściciela: nie da się utworzyć rezerwacji w złym stanie,
 * niezależnie od ścieżki (kasa, online, import).
 */
export class Reservation {
  constructor(readonly email: string, readonly seats: number, readonly total: Decimal) {
    if (!email.includes('@')) {
      throw new IllegalArgumentError(`niepoprawny email: ${email}`);
    }
    if (seats < 1) {
      throw new IllegalArgumentError(`liczba miejsc musi byc dodatnia: ${seats}`);
    }
    if (total.lessThan(0)) {
      throw new IllegalArgumentError(`kwota nie moze byc ujemna: ${total.toFixed(2)}`);
    }
  }
}
