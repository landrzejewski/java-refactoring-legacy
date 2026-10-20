import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import { AfterScreeningHandler } from './AfterScreeningHandler.js';
import { AutoRefundHandler } from './AutoRefundHandler.js';
import { CashierHandler } from './CashierHandler.js';
import { ManagerHandler } from './ManagerHandler.js';
import type { RefundHandler } from './RefundHandler.js';

/**
 * Krok 2: każda gałąź to ogniwo, a łańcuch if zastąpiła lista przeglądana po kolei.
 * Kolejność reguł jest już nazwana i widoczna w jednym miejscu. Koniec listy bez decyzji
 * to nadal jawny przypadek "brak uprawnionego zatwierdzającego".
 */
export class RefundApprover {
  private static readonly CASHIER_LIMIT = Money.of('100.00');
  private static readonly MANAGER_LIMIT = Money.of('1000.00');

  private readonly handlers: readonly RefundHandler[] = [
    new AfterScreeningHandler(),
    new AutoRefundHandler(),
    new CashierHandler(RefundApprover.CASHIER_LIMIT),
    new ManagerHandler(RefundApprover.MANAGER_LIMIT),
  ];

  decide(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    for (const handler of this.handlers) {
      const decision = handler.handle(request);
      if (decision !== undefined) {
        return decision;
      }
    }
    return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
  }
}
