namespace Training.Workshop.M5.S02PullUpField.Step3;

/// <summary>Krok 3: reguła normalizacji VIP zostaje w podklasie - przekazuje do bazy już znormalizowaną wartość.</summary>
public sealed class VipTicket : Ticket
{
    public VipTicket(string seat) : base(seat.ToUpperInvariant())
    {
    }

    public override string Describe()
    {
        return "VIP " + Seat;
    }
}
