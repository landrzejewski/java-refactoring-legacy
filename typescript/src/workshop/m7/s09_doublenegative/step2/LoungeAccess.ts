import type { LocalDate } from '../../../shared/time.js';
import type { Customer } from './Customer.js';
import type { Voucher } from './Voucher.js';

/** Krok 2: migracja użyć po jednym - każde !negatyw zamienione na pozytywny predykat. */
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
