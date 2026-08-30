package pl.training.module3.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

final class InMemoryQuoteRepositoryTest {
    @Test
    void storesQuote() {
        InMemoryQuoteRepository repository = new InMemoryQuoteRepository();
        DeliveryQuote quote = new DeliveryQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                new Parcel(new BigDecimal("3.00")),
                new BigDecimal("17.28"));

        repository.save(quote);

        assertEquals(List.of(quote), repository.quotes());
    }
}
