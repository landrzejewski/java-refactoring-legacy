namespace Training.Workshop.M5.S10FieldHiding.Step1;

/// <summary>
/// Krok 1: jedno pole zamiast dwóch slotów - prywatne, readonly, ustawiane przez konstruktor
/// (Encapsulate Field + parametr konstruktora). Podklasa przekazuje swoją wartość przez base(...).
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

    public static string Category()
    {
        return "BILET";
    }

    public string Label()
    {
        return Category() + ": " + _type;
    }
}
