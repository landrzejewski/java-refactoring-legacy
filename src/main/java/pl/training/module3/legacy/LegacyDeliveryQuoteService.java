package pl.training.module3.legacy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

public final class LegacyDeliveryQuoteService {
    private final List<DeliveryQuote> storedQuotes = new ArrayList<>();

    public DeliveryQuote createQuote(
            String customerEmail,
            ShippingMethod method,
            Parcel parcel) {
        BigDecimal price = switch (method) {
            case STANDARD -> {
                BigDecimal base = new BigDecimal("10.00")
                        .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
                yield money(base.add(
                        base.multiply(new BigDecimal("0.08"))));
            }
            case EXPRESS -> {
                BigDecimal base = new BigDecimal("20.00")
                        .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
                yield money(base.add(
                        base.multiply(new BigDecimal("0.08"))));
            }
        };

        DeliveryQuote quote = new DeliveryQuote(
                customerEmail,
                method,
                parcel,
                price);
        storedQuotes.add(quote);

        System.out.printf(
                "Quote ready for %s: %s costs %s%n",
                customerEmail,
                method,
                price);
        return quote;
    }

    public List<DeliveryQuote> storedQuotes() {
        return List.copyOf(storedQuotes);
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
