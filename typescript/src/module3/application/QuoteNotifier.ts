import type { DeliveryQuote } from '../domain/DeliveryQuote.js';

export interface QuoteNotifier {
  quoteCreated(quote: DeliveryQuote): void;
}
