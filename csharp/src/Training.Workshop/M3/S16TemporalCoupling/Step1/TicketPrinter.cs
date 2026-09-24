using System.Globalization;

namespace Training.Workshop.M3.S16TemporalCoupling.Step1;

/// <summary>
/// Krok 1: Change Signature - Print przyjmuje wszystko, czego potrzebuje,
/// pola i settery usunięte (Safe Delete). Protokół "najpierw ustaw, potem drukuj"
/// zamienił się w kompilowalny kontrakt, a obiekt jest bezstanowy i bezpieczny współbieżnie.
/// </summary>
public sealed class TicketPrinter
{
    public string Print(Screening screening, int seat, string buyer)
    {
        return "BILET " + screening.Title + " (" + screening.Format + ") "
            + screening.Start.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture)
            + ", miejsce " + seat + ", dla " + buyer.ToLowerInvariant();
    }
}
