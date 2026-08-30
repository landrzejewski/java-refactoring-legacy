package pl.training.module3.adapter;

import java.util.Objects;

import pl.training.module3.application.QuoteNotifier;
import pl.training.module3.domain.DeliveryQuote;

public final class ConsoleQuoteNotifier implements QuoteNotifier {
    @Override
    public void quoteCreated(DeliveryQuote quote) {
        Objects.requireNonNull(quote);

        System.out.printf(
                "Quote ready for %s: %s costs %s%n",
                quote.customerEmail(),
                quote.method(),
                quote.price());
    }
}
