using Training.Module3.Domain;

namespace Training.Module3.Application;

public interface IQuoteNotifier
{
    void QuoteCreated(DeliveryQuote quote);
}
