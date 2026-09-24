using System.Globalization;

namespace Training.Workshop.M3.S16TemporalCoupling.Start;

/// <summary>
/// Start: sprzężenie czasowe (protokół wywołań). Żeby wydrukować bilet, trzeba najpierw
/// wywołać SelectScreening, SelectSeat i ForBuyer - w dowolnej kolejności, ale wszystkie.
/// Nic w typach tego nie mówi: zapomniane wywołanie to NullReferenceException w Print
/// (operator "!" tylko ucisza ostrzeżenia nullable), a obiekt współdzielony przez dwie kasy
/// miesza dane klientów.
/// </summary>
public sealed class TicketPrinter
{
    private Screening? _screening;
    private int? _seat;
    private string? _buyer;

    public void SelectScreening(Screening screening)
    {
        _screening = screening;
    }

    public void SelectSeat(int seat)
    {
        _seat = seat;
    }

    public void ForBuyer(string email)
    {
        _buyer = email;
    }

    public string Print()
    {
        return "BILET " + _screening!.Title + " (" + _screening.Format + ") "
            + _screening.Start.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture)
            + ", miejsce " + _seat!.Value + ", dla " + _buyer!.ToLowerInvariant();
    }
}
