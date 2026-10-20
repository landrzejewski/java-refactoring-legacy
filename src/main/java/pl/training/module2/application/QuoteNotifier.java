package pl.training.module2.application;

import pl.training.module2.domain.DeliveryQuote;

@FunctionalInterface
public interface QuoteNotifier {
    void quoteCreated(DeliveryQuote quote);
}
