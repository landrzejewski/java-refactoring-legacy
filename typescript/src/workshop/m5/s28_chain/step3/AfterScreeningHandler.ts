import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 3: po rozpoczęciu seansu odrzuca i kończy łańcuch; wcześniej przekazuje dalej. */
export class AfterScreeningHandler implements RefundHandler {
  private readonly next: RefundHandler;

  constructor(next: RefundHandler) {
    this.next = requireNonNull(next, 'next');
  }

  handle(request: RefundRequest): RefundDecision {
    if (!request.requestedAt.isBefore(request.screeningStart)) {
      return RefundDecision.rejected('seans juz sie rozpoczal');
    }
    return this.next.handle(request);
  }
}
