using Training.Module2.Application;
using Training.Module2.Domain;

namespace Training.Module2.Adapter;

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
