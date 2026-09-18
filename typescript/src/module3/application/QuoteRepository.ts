import type { DeliveryQuote } from '../domain/DeliveryQuote.js';

export interface QuoteRepository {
  save(quote: DeliveryQuote): void;
}
