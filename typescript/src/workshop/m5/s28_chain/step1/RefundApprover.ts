import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Duration } from '../../../shared/time.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import { AfterScreeningHandler } from './AfterScreeningHandler.js';
import type { RefundHandler } from './RefundHandler.js';

/**
 * Krok 1: Extract Interface + Extract Class dla pierwszej gałęzi. Reszta łańcucha if bez zmian;
 * walidacja kwoty zostaje w approverze - to warunek wstępny, nie poziom zatwierdzania.
 */
export class RefundApprover {
  private static readonly CASHIER_LIMIT = Money.of('100.00');
  private static readonly MANAGER_LIMIT = Money.of('1000.00');

  private readonly afterScreening: RefundHandler = new AfterScreeningHandler();

  decide(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    const decision = this.afterScreening.handle(request);
    if (decision !== undefined) {
      return decision;
    } else if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
      return RefundDecision.approvedBy('AUTO');
    } else if (!request.amount.isGreaterThan(RefundApprover.CASHIER_LIMIT)) {
      return RefundDecision.approvedBy('CASHIER');
    } else if (!request.amount.isGreaterThan(RefundApprover.MANAGER_LIMIT)) {
      return RefundDecision.approvedBy('MANAGER');
    }
    return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
  }
}
