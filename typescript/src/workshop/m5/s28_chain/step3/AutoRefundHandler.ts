import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Duration } from '../../../shared/time.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 3: co najmniej 24 h przed seansem zatwierdza (bez limitu kwoty) i kończy łańcuch. */
export class AutoRefundHandler implements RefundHandler {
  private readonly next: RefundHandler;

  constructor(next: RefundHandler) {
    this.next = requireNonNull(next, 'next');
  }

  handle(request: RefundRequest): RefundDecision {
    if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
      return RefundDecision.approvedBy('AUTO');
    }
    return this.next.handle(request);
  }
}
