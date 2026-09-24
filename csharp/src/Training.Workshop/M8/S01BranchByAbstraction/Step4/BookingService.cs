namespace Training.Workshop.M8.S01BranchByAbstraction.Step4;

/// <summary>
/// Krok 4: usunięcie starej ścieżki. Po okresie obserwacji w trybie Modern kasujemy
/// LegacyTicketPricing i PricingMode - migracja jest zamknięta dopiero teraz.
/// </summary>
public sealed class BookingService
{
    private readonly ITicketPricing _pricing;

    public BookingService()
        : this(new ModernTicketPricing())
    {
    }

    public BookingService(ITicketPricing pricing)
    {
        _pricing = pricing;
    }

    public string Confirm(BookingRequest request)
    {
        return request.Screening.Title + ": " + string.Join(",", request.Seats)
            + " - do zaplaty " + _pricing.Total(request);
    }
}
