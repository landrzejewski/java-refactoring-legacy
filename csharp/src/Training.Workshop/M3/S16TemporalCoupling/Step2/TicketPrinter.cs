using System.Globalization;

namespace Training.Workshop.M3.S16TemporalCoupling.Step2;

/// <summary>Krok 2 (rozwiązanie): drukarka dostaje kompletny <see cref="TicketRequest"/>.</summary>
public sealed class TicketPrinter
{
    public string Print(TicketRequest request)
    {
        var screening = request.Screening;
        return "BILET " + screening.Title + " (" + screening.Format + ") "
            + screening.Start.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture)
            + ", miejsce " + request.Seat + ", dla " + request.Buyer.ToLowerInvariant();
    }
}
