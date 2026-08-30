package pl.training.module3.application;

import pl.training.module3.domain.DeliveryQuote;

@FunctionalInterface
public interface QuoteRepository {
    void save(DeliveryQuote quote);
}
