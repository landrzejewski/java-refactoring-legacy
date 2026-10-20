using Training.Module2.Domain;

namespace Training.Module2.Application;

public interface IQuoteNotifier
{
    void QuoteCreated(DeliveryQuote quote);
}
