namespace Training.Workshop.M5.S11ConstructorCall.Step2;

/// <summary>Krok 2: zwykła kolejność base(...), potem pola w ciele konstruktora - poprawność nie zależy już od kolejności inicjalizacji.</summary>
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
