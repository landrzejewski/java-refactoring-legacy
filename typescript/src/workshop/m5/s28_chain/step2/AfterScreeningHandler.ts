import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 2: ogniwo - po rozpoczęciu seansu zwrotu nie ma. */
export class AfterScreeningHandler implements RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined {
    if (!request.requestedAt.isBefore(request.screeningStart)) {
      return RefundDecision.rejected('seans juz sie rozpoczal');
    }
    return undefined;
  }
}
