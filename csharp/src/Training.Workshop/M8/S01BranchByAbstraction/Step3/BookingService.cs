namespace Training.Workshop.M8.S01BranchByAbstraction.Step3;

/// <summary>
/// Krok 3: przełącznik w jednym miejscu. Domyślnie Legacy - wdrożenie nowego kodu
/// nie zmienia zachowania, dopiero konfiguracja (Modern) je przełącza.
/// </summary>
public sealed class BookingService
{
    private readonly ITicketPricing _pricing;

    public BookingService()
        : this(PricingMode.Legacy)
    {
    }

    public BookingService(PricingMode mode)
        : this(mode switch
        {
            PricingMode.Legacy => new LegacyTicketPricing(),
            PricingMode.Modern => new ModernTicketPricing(),
            _ => throw new ArgumentOutOfRangeException(nameof(mode), mode, null),
        })
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
