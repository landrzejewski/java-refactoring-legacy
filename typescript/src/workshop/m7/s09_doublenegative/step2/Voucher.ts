import type { LocalDate } from '../../../shared/time.js';

/** Krok 1 (bez zmian w kroku 2): pozytywny predykat isExpired() delegujący do isNotExpired(). */
export class Voucher {
  constructor(readonly code: string, readonly validUntil: LocalDate) {}

  isNotExpired(today: LocalDate): boolean {
    return !today.isAfter(this.validUntil);
  }

  isExpired(today: LocalDate): boolean {
    return !this.isNotExpired(today);
  }
}
