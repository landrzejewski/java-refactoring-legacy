import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import { AfterScreeningHandler } from './AfterScreeningHandler.js';
import { AutoRefundHandler } from './AutoRefundHandler.js';
import { CashierHandler } from './CashierHandler.js';
import { ManagerHandler } from './ManagerHandler.js';
import { NoApproverHandler } from './NoApproverHandler.js';
import type { RefundHandler } from './RefundHandler.js';

/**
 * Krok 3: jawne składanie łańcucha. Bez argumentu powstaje standardowa ścieżka, z argumentem -
 * dowolna głowa łańcucha (np. festiwal bez zwrotów automatycznych). Walidacja kwoty zostaje
 * tutaj: to warunek wstępny, a nie poziom zatwierdzania.
 * (Dwa konstruktory z Javy to tutaj jeden z parametrem opcjonalnym.)
 */
export class RefundApprover {
  private static readonly CASHIER_LIMIT = Money.of('100.00');
  private static readonly MANAGER_LIMIT = Money.of('1000.00');

  private readonly chain: RefundHandler;

  constructor(chain: RefundHandler = RefundApprover.standardChain()) {
    this.chain = requireNonNull(chain, 'chain');
  }

  private static standardChain(): RefundHandler {
    return new AfterScreeningHandler(
      new AutoRefundHandler(
        new CashierHandler(RefundApprover.CASHIER_LIMIT,
          new ManagerHandler(RefundApprover.MANAGER_LIMIT,
            new NoApproverHandler()))));
  }

  decide(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    return this.chain.handle(request);
  }
}
