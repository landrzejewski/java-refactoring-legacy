package pl.training.module4.stage3;

import java.util.Locale;
import java.util.Objects;

import pl.training.module4.model.RentalRequest;
import pl.training.module4.pricing.PriceBreakdown;
import pl.training.module4.pricing.RentalPricing;

public final class RentalQuoteService {
    private final RentalPricing pricing;

    public RentalQuoteService() {
        this(RentalPricing.standard());
    }

    public RentalQuoteService(RentalPricing pricing) {
        this.pricing = Objects.requireNonNull(pricing);
    }

    public String createQuote(RentalRequest request) {
        Objects.requireNonNull(request, "request");

        PriceBreakdown price = pricing.calculate(request);
        return buildDocument(request, price);
    }

    private static String buildDocument(
            RentalRequest request,
            PriceBreakdown price) {
        return String.format(
                Locale.ROOT,
                """
                RENTAL QUOTE
                Customer: %s
                Equipment: %s
                Days: %d
                Base: %s
                Discount: %s
                Insurance: %s
                Delivery: %s
                Net: %s
                VAT: %s
                Total: %s
                """,
                request.customerName().strip().toUpperCase(Locale.ROOT),
                request.equipmentType(),
                request.days(),
                price.baseRentalCost().toPlainString(),
                price.discount().toPlainString(),
                price.insuranceCost().toPlainString(),
                price.deliveryCost().toPlainString(),
                price.netAmount().toPlainString(),
                price.vat().toPlainString(),
                price.total().toPlainString());
    }
}
