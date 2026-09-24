namespace Training.Workshop.M5.S02PullUpField.Step2;

/// <summary>
/// Krok 2: ujednolicenie cyklu życia - miejsce przez konstruktor, pole readonly, setter usunięty.
/// Dopiero teraz wszystkie trzy pola "_seat" mają ten sam typ, znaczenie i moment inicjalizacji.
/// </summary>
public sealed class StandardTicket : Ticket
{
    private readonly string _seat;

    public StandardTicket(string seat)
    {
        _seat = seat;
    }

    public string Seat => _seat;

    public override string Describe()
    {
        return "NORMAL " + _seat;
    }
}
