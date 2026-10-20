import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 2: poziom MANAGER zatwierdza zwroty do swojego limitu (włącznie). */
export class ManagerHandler implements RefundHandler {
  private readonly limit: Money;

  constructor(limit: Money) {
    this.limit = requireNonNull(limit, 'limit');
  }

  handle(request: RefundRequest): RefundDecision | undefined {
    if (!request.amount.isGreaterThan(this.limit)) {
      return RefundDecision.approvedBy('MANAGER');
    }
    return undefined;
  }
}
