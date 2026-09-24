using Training.Workshop.Shared;

namespace Training.Workshop.M8.S02StranglerFig;

/// <summary>
/// Wspólna baza rezerwacji. W trakcie duszenia stary i nowy kod korzystają z tych samych danych,
/// dlatego raport legacy widzi rezerwacje przyjęte już przez nowy moduł.
/// </summary>
public sealed class BookingLedger
{
    private readonly List<Booking> _bookings = [];
    private int _sequence = 1;

    public string NextId()
    {
        return "B" + _sequence++;
    }

    public void Add(Booking booking)
    {
        _bookings.Add(booking);
    }

    public IReadOnlyList<Booking> All()
    {
        return _bookings.ToList();
    }

    /// <summary>Wiersz bazy: wartość biletów i opłaty rezerwacyjne osobno.</summary>
    public sealed record Booking(string Id, string Email, string Title, int Tickets, Money TicketsValue, Money Fees);
}
