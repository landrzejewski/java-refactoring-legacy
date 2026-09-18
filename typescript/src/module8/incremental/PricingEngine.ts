import type { PriceQuote } from './PriceQuote.js';
import type { PriceRequest } from './PriceRequest.js';

/**
 * Czysta granica obliczeniowa używana podczas migracji. Implementacje nie
 * wykonują operacji wejścia-wyjścia ani nie modyfikują zewnętrznego stanu.
 */
export interface PricingEngine {
  quote(request: PriceRequest): PriceQuote;
}
