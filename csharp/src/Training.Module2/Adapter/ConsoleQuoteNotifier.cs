using System.Globalization;
using Training.Module2.Application;
using Training.Module2.Domain;

namespace Training.Module2.Adapter;

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
