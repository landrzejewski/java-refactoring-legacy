package pl.training.module2.application;

import pl.training.module2.domain.DeliveryQuote;

@FunctionalInterface
public interface QuoteRepository {
    void save(DeliveryQuote quote);
}
