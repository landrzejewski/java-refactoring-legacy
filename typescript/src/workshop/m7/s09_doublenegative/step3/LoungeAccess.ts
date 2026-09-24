import type { LocalDate } from '../../../shared/time.js';
import type { Customer } from './Customer.js';
import type { Voucher } from './Voucher.js';

/** Krok 3 (rozwiązanie): LoungeAccess jak w kroku 2 - czyta się bez odwracania w głowie. */
export class LoungeAccess {
  canEnter(customer: Customer, voucher: Voucher | null, today: LocalDate): boolean {
    if (customer.vip) {
      return true;
    }
    if (voucher === null) {
      return false;
    }
    if (voucher.isExpired(today)) {
      return false;
    }
    return true;
  }

  badge(customer: Customer): string {
    return customer.vip ? 'VIP' : 'STANDARD';
  }
}
