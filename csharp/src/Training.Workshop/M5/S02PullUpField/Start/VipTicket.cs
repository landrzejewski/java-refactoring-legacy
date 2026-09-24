namespace Training.Workshop.M5.S02PullUpField.Start;

/// <summary>Start: miejsce normalizowane do wielkich liter - ta reguła dotyczy tylko VIP.</summary>
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
