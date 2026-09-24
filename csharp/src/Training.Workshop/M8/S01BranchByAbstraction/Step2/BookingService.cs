namespace Training.Workshop.M8.S01BranchByAbstraction.Step2;

/// <summary>Krok 2: klient zależy od abstrakcji ITicketPricing; domyślnie działa stara implementacja.</summary>
public sealed class BookingService
{
    private readonly ITicketPricing _pricing;

    public BookingService()
        : this(new LegacyTicketPricing())
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
