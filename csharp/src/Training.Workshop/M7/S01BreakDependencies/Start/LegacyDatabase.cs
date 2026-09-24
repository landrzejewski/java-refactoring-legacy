namespace Training.Workshop.M7.S01BreakDependencies.Start;

/// <summary>Produkcyjna baza. Konstruktor otwiera połączenie - poza serwerownią kina to się nie uda.</summary>
public sealed class LegacyDatabase
{
    public LegacyDatabase()
    {
        throw new InvalidOperationException("brak polaczenia z jdbc:oracle:thin:@prod-db:1521/CINEMA");
    }

    public IReadOnlyList<PaidBooking> PaidBookings()
    {
        return [];
    }

    public void MarkReminded(string bookingId)
    {
        // UPDATE bookings SET reminded = 1 WHERE id = ?
    }
}
