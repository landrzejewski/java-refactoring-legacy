package pl.training.module4;

import java.math.BigDecimal;
import java.util.EnumMap;

import pl.training.module4.encapsulation.EquipmentCatalog;
import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;
import pl.training.module4.pricing.RentalPricing;

public final class Module4Examples {
    private Module4Examples() {
    }

    public static void main(String[] args) {
        RentalRequest request = new RentalRequest(
                " Acme ",
                EquipmentType.GENERATOR,
                8,
                true,
                true);

        String legacyQuote = new pl.training.module4.stage0.RentalQuoteService()
                .createQuote(request);
        String refactoredQuote = new pl.training.module4.stage3.RentalQuoteService(
                RentalPricing.standard())
                .createQuote(request);

        if (!legacyQuote.equals(refactoredQuote)) {
            throw new IllegalStateException("Refactoring changed the quote");
        }

        EnumMap<EquipmentType, BigDecimal> sourceRates =
                new EnumMap<>(EquipmentType.class);
        sourceRates.put(EquipmentType.DRILL, new BigDecimal("39.99"));
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                sourceRates);
        var snapshot = catalog.dailyRates();
        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.00"));

        System.out.print(refactoredQuote);
        System.out.println("Quote stages equivalent: true");
        System.out.println(
                "Catalog snapshot isolated: "
                        + snapshot.get(EquipmentType.DRILL)
                                .equals(new BigDecimal("39.99")));
    }
}
