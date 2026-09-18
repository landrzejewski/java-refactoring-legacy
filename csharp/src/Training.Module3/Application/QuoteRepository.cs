using Training.Module3.Domain;

namespace Training.Module3.Application;

public interface IQuoteRepository
{
    void Save(DeliveryQuote quote);
}
