using System.Globalization;

namespace Training.Workshop.M8.S01BranchByAbstraction.Step1;

/// <summary>Krok 1: potwierdzenie deleguje wyliczenie ceny do wydzielonej klasy LegacyTicketPricing.</summary>
public sealed class BookingService
{
    private readonly LegacyTicketPricing _pricing = new();

    public string Confirm(BookingRequest request)
    {
        double total = _pricing.Total(request);
        return request.Screening.Title + ": " + string.Join(",", request.Seats)
            + " - do zaplaty " + total.ToString("F2", CultureInfo.InvariantCulture);
    }
}
