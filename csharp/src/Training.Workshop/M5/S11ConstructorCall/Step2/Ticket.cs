namespace Training.Workshop.M5.S11ConstructorCall.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): konstruktor nie woła już metody wirtualnej. Pole _label usunięte
/// (Replace Field with Query) - etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany.
/// </summary>
public class Ticket
{
    private readonly string _seat;

    public Ticket(string seat)
    {
        _seat = seat;
    }

    protected virtual string Describe()
    {
        return "Miejsce " + _seat;
    }

    public string Label()
    {
        return Describe();
    }
}
