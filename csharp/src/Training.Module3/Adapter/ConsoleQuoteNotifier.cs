using System.Globalization;
using Training.Module3.Application;
using Training.Module3.Domain;

namespace Training.Module3.Adapter;

public sealed class ConsoleQuoteNotifier : IQuoteNotifier
{
    public void QuoteCreated(DeliveryQuote quote)
    {
        ArgumentNullException.ThrowIfNull(quote);

        Console.WriteLine(string.Format(
            CultureInfo.InvariantCulture,
            "Quote ready for {0}: {1} costs {2}",
            quote.CustomerEmail,
            quote.Method.ToString().ToUpperInvariant(),
            quote.Price));
    }
}
