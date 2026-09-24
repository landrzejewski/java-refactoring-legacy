namespace Training.Workshop.M5.S02PullUpField.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Pull Members Up dla pola <c>_seat</c> i akcesora <c>Seat</c>.
/// Pole w bazie jest private readonly i ustawiane przez base(...) - nie surowe protected.
/// </summary>
public abstract class Ticket
{
    private readonly string _seat;

    protected Ticket(string seat)
    {
        _seat = seat;
    }

    public string Seat => _seat;

    public abstract string Describe();
}
