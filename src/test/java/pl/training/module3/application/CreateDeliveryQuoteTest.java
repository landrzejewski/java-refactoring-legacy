package pl.training.module3.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module3.application.CreateDeliveryQuote.Command;
import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.FuelSurcharge;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;
import pl.training.module3.domain.StandardDeliveryPricePolicy;

final class CreateDeliveryQuoteTest {
    @Test
    void calculatesStoresAndNotifiesAboutQuote() {
        List<DeliveryQuote> savedQuotes = new ArrayList<>();
        List<DeliveryQuote> notifications = new ArrayList<>();
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator(),
                savedQuotes::add,
                notifications::add);

        DeliveryQuote result = useCase.execute(new Command(
                "developer@example.com",
                ShippingMethod.STANDARD,
                new Parcel(new BigDecimal("3.00"))));

        assertEquals(new BigDecimal("17.28"), result.price());
        assertEquals(List.of(result), savedQuotes);
        assertEquals(List.of(result), notifications);
    }

    @Test
    void doesNotNotifyWhenSavingFails() {
        List<DeliveryQuote> notifications = new ArrayList<>();
        QuoteRepository failingRepository = quote -> {
            throw new IllegalStateException("Storage unavailable");
        };
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator(),
                failingRepository,
                notifications::add);

        assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(new Command(
                        "developer@example.com",
                        ShippingMethod.STANDARD,
                        new Parcel(new BigDecimal("3.00")))));
        assertTrue(notifications.isEmpty());
    }

    private static DeliveryPriceCalculator calculator() {
        FuelSurcharge surcharge = new FuelSurcharge(new BigDecimal("0.08"));
        return new DeliveryPriceCalculator(List.of(
                new StandardDeliveryPricePolicy(surcharge)));
    }
}
