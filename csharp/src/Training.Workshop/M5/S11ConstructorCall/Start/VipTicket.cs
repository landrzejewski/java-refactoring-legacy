namespace Training.Workshop.M5.S11ConstructorCall.Start;

/// <summary>
/// Start: pole _lounge jest jeszcze null, gdy baza woła Describe() - etykieta na zawsze ma pusty salonik.
/// Kompilator nullable niczego nie zgłasza: pole jest "string", a nie "string?" - adnotacje kłamią w trakcie konstrukcji.
/// </summary>
public class VipTicket : Ticket
{
    private readonly string _lounge;

    public VipTicket(string seat, string lounge) : base(seat)
    {
        _lounge = lounge;
    }

    protected override string Describe()
    {
        return base.Describe() + " (VIP: " + _lounge + ")";
    }
}
