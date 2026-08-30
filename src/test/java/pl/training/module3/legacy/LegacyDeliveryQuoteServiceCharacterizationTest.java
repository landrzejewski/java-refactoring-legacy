package pl.training.module3.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

final class LegacyDeliveryQuoteServiceCharacterizationTest {
    @Test
    void documentsStandardDeliveryPriceAndStorage() {
        LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();
        Parcel parcel = new Parcel(new BigDecimal("3.00"));

        DeliveryQuote quote = service.createQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel);

        assertEquals(new BigDecimal("17.28"), quote.price());
        assertEquals(List.of(quote), service.storedQuotes());
    }

    @Test
    void documentsExpressDeliveryPrice() {
        LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();

        DeliveryQuote quote = service.createQuote(
                "developer@example.com",
                ShippingMethod.EXPRESS,
                new Parcel(new BigDecimal("3.00")));

        assertEquals(new BigDecimal("31.32"), quote.price());
    }
}
