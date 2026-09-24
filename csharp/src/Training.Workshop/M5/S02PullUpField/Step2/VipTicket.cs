namespace Training.Workshop.M5.S02PullUpField.Step2;

/// <summary>Krok 2: bez zmian - normalizacja VIP zostaje.</summary>
public sealed class VipTicket : Ticket
{
    private readonly string _seat;

    public VipTicket(string seat)
    {
        _seat = seat.ToUpperInvariant();
    }

    public string Seat => _seat;

    public override string Describe()
    {
        return "VIP " + _seat;
    }
}
