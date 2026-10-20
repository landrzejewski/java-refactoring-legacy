using Training.Module2.Domain;

namespace Training.Module2.Application;

public interface IQuoteRepository
{
    void Save(DeliveryQuote quote);
}
