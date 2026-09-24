import { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { Refundable } from './Refundable.js';

/** Krok 3: po migracji klientów stare metody usunięte - został jeden kontrakt. */
export class RefundService {
  private static readonly FEE = Money.of('3.00');

  refund(refundable: Refundable, now: LocalDateTime): Money {
    return refundable.refundableAmount(now).minus(RefundService.FEE).max(Money.ZERO);
  }
}
