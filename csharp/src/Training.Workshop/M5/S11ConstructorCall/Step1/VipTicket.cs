namespace Training.Workshop.M5.S11ConstructorCall.Step1;

/// <summary>
/// Krok 1: szybka naprawa - konstruktor główny (primary constructor) i inicjalizator pola.
/// Inicjalizatory pól w C# wykonują się PRZED wywołaniem konstruktora bazy, więc override widzi wartość
/// (odpowiednik prologu konstruktora z Javy 25). Działa, ale kruche: każda nowa podklasa musi o tym pamiętać.
/// </summary>
public class VipTicket(string seat, string lounge) : Ticket(seat)
{
    private readonly string _lounge = lounge;

    protected override string Describe()
    {
        return base.Describe() + " (VIP: " + _lounge + ")";
    }
}
