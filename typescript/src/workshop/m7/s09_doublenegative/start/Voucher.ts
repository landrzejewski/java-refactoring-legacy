import type { LocalDate } from '../../../shared/time.js';

/** Start: negatywny predykat isNotExpired - czytelnik musi go odwracać w głowie. */
export class Voucher {
  constructor(readonly code: string, readonly validUntil: LocalDate) {}

  isNotExpired(today: LocalDate): boolean {
    return !today.isAfter(this.validUntil);
  }
}
