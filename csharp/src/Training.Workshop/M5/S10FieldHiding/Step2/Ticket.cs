namespace Training.Workshop.M5.S10FieldHiding.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Category() jako wirtualna metoda instancji - nadpisywalna i wybierana dynamicznie.
/// Teraz Label() daje ten sam wynik bez względu na typ referencji.
/// </summary>
public class Ticket
{
    private readonly string _type;

    public Ticket() : this("NORMAL")
    {
    }

    protected Ticket(string type)
    {
        _type = type;
    }

    public string Type => _type;

    public virtual string Category()
    {
        return "BILET";
    }

    public string Label()
    {
        return Category() + ": " + _type;
    }
}
