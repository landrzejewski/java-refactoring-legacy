package pl.training.module3.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.training.module3.application.QuoteRepository;
import pl.training.module3.domain.DeliveryQuote;

public final class InMemoryQuoteRepository implements QuoteRepository {
    private final List<DeliveryQuote> quotes = new ArrayList<>();

    @Override
    public void save(DeliveryQuote quote) {
        quotes.add(Objects.requireNonNull(quote));
    }

    public List<DeliveryQuote> quotes() {
        return List.copyOf(quotes);
    }
}
