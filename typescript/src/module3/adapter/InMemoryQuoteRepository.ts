import { requireNonNull } from '../../shared/requireNonNull.js';
import type { QuoteRepository } from '../application/QuoteRepository.js';
import type { DeliveryQuote } from '../domain/DeliveryQuote.js';

export class InMemoryQuoteRepository implements QuoteRepository {
  private readonly storedQuotes: DeliveryQuote[] = [];

  save(quote: DeliveryQuote): void {
    this.storedQuotes.push(requireNonNull(quote));
  }

  quotes(): readonly DeliveryQuote[] {
    return Object.freeze([...this.storedQuotes]);
  }
}
