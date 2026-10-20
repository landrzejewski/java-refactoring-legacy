import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 3: poziom CASHIER - w limicie zatwierdza i kończy, powyżej eskaluje do następnika. */
export class CashierHandler implements RefundHandler {
  private readonly limit: Money;
  private readonly next: RefundHandler;

  constructor(limit: Money, next: RefundHandler) {
    this.limit = requireNonNull(limit, 'limit');
    this.next = requireNonNull(next, 'next');
  }

  handle(request: RefundRequest): RefundDecision {
    if (!request.amount.isGreaterThan(this.limit)) {
      return RefundDecision.approvedBy('CASHIER');
    }
    return this.next.handle(request);
  }
}
