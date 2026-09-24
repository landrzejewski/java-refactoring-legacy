namespace Training.Workshop.M5.S11ConstructorCall.Step1;

/// <summary>Krok 1: baza bez zmian - naprawa lokalna w podklasie.</summary>
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
