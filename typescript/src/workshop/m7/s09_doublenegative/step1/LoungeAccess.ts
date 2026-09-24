import type { LocalDate } from '../../../shared/time.js';
import type { Customer } from './Customer.js';
import type { Voucher } from './Voucher.js';

/** Krok 1: LoungeAccess bez zmian - dodaliśmy tylko pozytywne predykaty w Customer i Voucher. */
export class LoungeAccess {
  canEnter(customer: Customer, voucher: Voucher | null, today: LocalDate): boolean {
    if (!customer.notVip) {
      return true;
    }
    if (voucher === null) {
      return false;
    }
    if (!voucher.isNotExpired(today)) {
      return false;
    }
    return true;
  }

  badge(customer: Customer): string {
    return !customer.notVip ? 'VIP' : 'STANDARD';
  }
}
