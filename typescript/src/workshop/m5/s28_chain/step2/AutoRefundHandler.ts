import { Duration } from '../../../shared/time.js';
import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 2: co najmniej 24 h przed seansem zwrot jest automatyczny - bez limitu kwoty. */
export class AutoRefundHandler implements RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined {
    if (Duration.between(request.requestedAt, request.screeningStart).toHours() >= 24) {
      return RefundDecision.approvedBy('AUTO');
    }
    return undefined;
  }
}
