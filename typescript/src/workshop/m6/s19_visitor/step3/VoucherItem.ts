import type { Money } from '../../../shared/Money.js';

/** Krok 3: voucher - czyste dane, bez accept. */
export class VoucherItem {
  readonly kind = 'voucher';

  constructor(readonly code: string, readonly value: Money) {}
}
