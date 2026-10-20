import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Duration } from '../../../shared/time.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';

/**
 * Start: kto zatwierdza zwrot, rozstrzyga łańcuch if/else. Kolejność warunków jest regułą
 * biznesową (np. zwrot automatyczny nie ma limitu kwoty, bo stoi przed kasjerem), ale nie ma
 * nazwy - zmiana kolejności, dodanie poziomu albo innej ścieżki dla festiwalu oznacza edycję
 * tej jednej metody. Gdy nikt nie jest uprawniony, prośba "spada" z końca łańcucha.
 */
export class RefundApprover {
  private static readonly CASHIER_LIMIT = Money.of('100.00');
  private static readonly MANAGER_LIMIT = Money.of('1000.00');

  decide(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(Money.ZERO)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    if (!request.requestedAt.isBefore(request.screeningStart)) {
      return RefundDecision.rejected('seans juz sie rozpoczal');
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
