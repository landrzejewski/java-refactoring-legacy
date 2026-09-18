import { requireNonNull } from '../../shared/requireNonNull.js';
import type { QuoteNotifier } from '../application/QuoteNotifier.js';
import type { DeliveryQuote } from '../domain/DeliveryQuote.js';

export class ConsoleQuoteNotifier implements QuoteNotifier {
  quoteCreated(quote: DeliveryQuote): void {
    requireNonNull(quote);

    console.log(
      `Quote ready for ${quote.customerEmail}: ${quote.method} costs ${quote.price.toFixed(2)}`);
  }
}
