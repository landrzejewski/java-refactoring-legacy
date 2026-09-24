namespace Training.Workshop.M5.S16SerializationProxy.Step2;

/// <summary>
/// Krok 2: baza nie jest już kontraktem danych - formatem zarządza proxy serializacji w podklasie,
/// więc przyszłe ruchy w hierarchii nie zmienią postaci zapisu.
/// </summary>
public abstract class Ticket
{
    private readonly string _title;
    private readonly string _seat;

    protected Ticket(string title, string seat)
    {
        _title = title;
        _seat = seat;
    }

    public string Title => _title;

    public string Seat => _seat;
}
