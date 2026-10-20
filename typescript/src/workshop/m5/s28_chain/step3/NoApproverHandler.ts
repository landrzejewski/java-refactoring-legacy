import { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';
import type { RefundHandler } from './RefundHandler.js';

/** Krok 3: jawny koniec łańcucha - żądanie nie może po cichu "spaść" bez odpowiedzi. */
export class NoApproverHandler implements RefundHandler {
  handle(_request: RefundRequest): RefundDecision {
    return RefundDecision.rejected('brak uprawnionego zatwierdzajacego');
  }
}
