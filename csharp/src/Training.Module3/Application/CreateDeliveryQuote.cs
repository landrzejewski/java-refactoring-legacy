using Training.Module3.Domain;

namespace Training.Module3.Application;

public sealed class CreateDeliveryQuote
{
    private readonly DeliveryPriceCalculator priceCalculator;
    private readonly IQuoteRepository repository;
    private readonly IQuoteNotifier notifier;

    public CreateDeliveryQuote(
        DeliveryPriceCalculator priceCalculator,
        IQuoteRepository repository,
        IQuoteNotifier notifier)
    {
        ArgumentNullException.ThrowIfNull(priceCalculator);
        ArgumentNullException.ThrowIfNull(repository);
        ArgumentNullException.ThrowIfNull(notifier);
        this.priceCalculator = priceCalculator;
        this.repository = repository;
        this.notifier = notifier;
    }

    public DeliveryQuote Execute(Command command)
    {
        ArgumentNullException.ThrowIfNull(command);

        DeliveryQuote quote = new(
            command.CustomerEmail,
            command.Method,
            command.Parcel,
            priceCalculator.PriceFor(command.Method, command.Parcel));

        repository.Save(quote);
        notifier.QuoteCreated(quote);
        return quote;
    }

    public sealed record Command(
        string CustomerEmail,
        ShippingMethod Method,
        Parcel Parcel)
    {
        public string CustomerEmail { get; } =
            CustomerEmail ?? throw new ArgumentNullException(nameof(CustomerEmail));

        public Parcel Parcel { get; } =
            Parcel ?? throw new ArgumentNullException(nameof(Parcel));
    }
}
