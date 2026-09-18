namespace Training.Module8.Incremental;

/// <summary>
/// Czysta granica obliczeniowa używana podczas migracji. Implementacje nie
/// wykonują operacji wejścia-wyjścia ani nie modyfikują zewnętrznego stanu.
/// </summary>
public interface IPricingEngine
{
    PriceQuote Quote(PriceRequest request);

    /// <summary>Odpowiednik lambdy dla interfejsu funkcyjnego z Javy.</summary>
    static IPricingEngine Of(Func<PriceRequest, PriceQuote> quote)
    {
        ArgumentNullException.ThrowIfNull(quote);
        return new DelegatingPricingEngine(quote);
    }

    private sealed class DelegatingPricingEngine(
        Func<PriceRequest, PriceQuote> quote) : IPricingEngine
    {
        public PriceQuote Quote(PriceRequest request) => quote(request);
    }
}
