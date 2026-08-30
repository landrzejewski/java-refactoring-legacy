package pl.training.module3.application;

import java.util.Objects;

import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

public final class CreateDeliveryQuote {
    private final DeliveryPriceCalculator priceCalculator;
    private final QuoteRepository repository;
    private final QuoteNotifier notifier;

    public CreateDeliveryQuote(
            DeliveryPriceCalculator priceCalculator,
            QuoteRepository repository,
            QuoteNotifier notifier) {
        this.priceCalculator = Objects.requireNonNull(priceCalculator);
        this.repository = Objects.requireNonNull(repository);
        this.notifier = Objects.requireNonNull(notifier);
    }

    public DeliveryQuote execute(Command command) {
        Objects.requireNonNull(command, "command");

        DeliveryQuote quote = new DeliveryQuote(
                command.customerEmail(),
                command.method(),
                command.parcel(),
                priceCalculator.priceFor(command.method(), command.parcel()));

        repository.save(quote);
        notifier.quoteCreated(quote);
        return quote;
    }

    public record Command(
            String customerEmail,
            ShippingMethod method,
            Parcel parcel) {
        public Command {
            Objects.requireNonNull(customerEmail, "customerEmail");
            Objects.requireNonNull(method, "method");
            Objects.requireNonNull(parcel, "parcel");
        }
    }
}
