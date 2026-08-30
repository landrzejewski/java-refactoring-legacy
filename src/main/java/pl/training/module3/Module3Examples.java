package pl.training.module3;

import java.math.BigDecimal;
import java.util.List;

import pl.training.module3.adapter.ConsoleQuoteNotifier;
import pl.training.module3.adapter.InMemoryQuoteRepository;
import pl.training.module3.application.CreateDeliveryQuote;
import pl.training.module3.application.CreateDeliveryQuote.Command;
import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.ExpressDeliveryPricePolicy;
import pl.training.module3.domain.FuelSurcharge;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;
import pl.training.module3.domain.StandardDeliveryPricePolicy;
import pl.training.module3.legacy.LegacyDeliveryQuoteService;

public final class Module3Examples {
    private Module3Examples() {
    }

    public static void main(String[] args) {
        Parcel parcel = new Parcel(new BigDecimal("3.00"));
        LegacyDeliveryQuoteService legacy = new LegacyDeliveryQuoteService();
        DeliveryQuote legacyQuote = legacy.createQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel);

        FuelSurcharge fuelSurcharge = new FuelSurcharge(
                new BigDecimal("0.08"));
        DeliveryPriceCalculator calculator = new DeliveryPriceCalculator(
                List.of(
                        new StandardDeliveryPricePolicy(fuelSurcharge),
                        new ExpressDeliveryPricePolicy(fuelSurcharge)));
        InMemoryQuoteRepository repository = new InMemoryQuoteRepository();
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator,
                repository,
                new ConsoleQuoteNotifier());

        DeliveryQuote refactoredQuote = useCase.execute(new Command(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel));

        System.out.println(
                "Legacy and refactored prices equal: "
                        + legacyQuote.price().equals(refactoredQuote.price()));
        System.out.println("Stored quotes: " + repository.quotes().size());
    }
}
