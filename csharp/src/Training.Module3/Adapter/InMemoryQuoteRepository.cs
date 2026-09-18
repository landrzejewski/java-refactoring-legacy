using Training.Module3.Application;
using Training.Module3.Domain;

namespace Training.Module3.Adapter;

public sealed class InMemoryQuoteRepository : IQuoteRepository
{
    private readonly List<DeliveryQuote> quotes = [];

    public void Save(DeliveryQuote quote)
    {
        ArgumentNullException.ThrowIfNull(quote);
        quotes.Add(quote);
    }

    public IReadOnlyList<DeliveryQuote> Quotes() => [.. quotes];
}
