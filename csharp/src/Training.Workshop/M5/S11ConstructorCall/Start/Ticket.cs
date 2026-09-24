namespace Training.Workshop.M5.S11ConstructorCall.Start;

/// <summary>
/// Start: konstruktor bazy woła metodę wirtualną <c>Describe()</c>, żeby zapamiętać etykietę.
/// Override w podklasie wykona się, ZANIM ciało konstruktora podklasy przypisze jej pola.
/// (Przed tym ostrzega reguła analizatora CA2214 "Do not call overridable methods in constructors".)
/// </summary>
public class Ticket
{
    private readonly string _seat;
    private readonly string _label;

    public Ticket(string seat)
    {
        _seat = seat;
        _label = Describe();
    }

    protected virtual string Describe()
    {
        return "Miejsce " + _seat;
    }

    public string Label()
    {
        return _label;
    }
}
