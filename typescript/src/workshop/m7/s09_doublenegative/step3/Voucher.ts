import type { LocalDate } from '../../../shared/time.js';

/** Krok 3: isExpired() jest teraz jedyną definicją reguły; isNotExpired() usunięte (Safe Delete). */
export class Voucher {
  constructor(readonly code: string, readonly validUntil: LocalDate) {}

  isExpired(today: LocalDate): boolean {
    return today.isAfter(this.validUntil);
  }
}
