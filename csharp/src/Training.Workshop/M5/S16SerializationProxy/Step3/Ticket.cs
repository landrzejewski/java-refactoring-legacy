namespace Training.Workshop.M5.S16SerializationProxy.Step3;

/// <summary>Krok 3: bez zmian.</summary>
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
