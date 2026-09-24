import type { Decimal } from 'decimal.js';

/**
 * Start: anemiczny model z publicznymi, zmiennymi polami (odpowiednik JavaBean z setterami).
 * Model nie pilnuje żadnego inwariantu - poprawność zależy od tego, czy KAŻDY,
 * kto go tworzy, pamięta o walidacji.
 */
export class Reservation {
  email!: string;
  seats = 0;
  total!: Decimal;
}
