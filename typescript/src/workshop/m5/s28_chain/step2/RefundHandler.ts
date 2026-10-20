import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';

/**
 * Krok 2: bez zmian - ogniwo decyzji - pusta odpowiedź znaczy "to nie moja sprawa, pytaj dalej".
 * (Optional<RefundDecision> z Javy to tutaj RefundDecision | undefined.)
 */
export interface RefundHandler {
  handle(request: RefundRequest): RefundDecision | undefined;
}
