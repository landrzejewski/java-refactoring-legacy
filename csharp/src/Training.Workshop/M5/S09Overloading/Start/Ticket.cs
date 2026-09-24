using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Start;

/// <summary>
/// Start: stan po Extract Superclass (wcześniej StandardTicket i StudentTicket były niezależne).
/// Pułapka 2: <c>Equals(Ticket)</c> to PRZECIĄŻENIE, nie nadpisanie Equals(object) - kolekcje go nie widzą.
/// </summary>
public class Ticket
{
    private readonly string _title;
    private readonly Money _basePrice;

    public Ticket(string title, Money basePrice)
    {
        _title = title;
        _basePrice = basePrice;
    }

    public string Title => _title;

    public Money BasePrice => _basePrice;

    public bool Equals(Ticket other)
    {
        return _title == other._title && _basePrice.Equals(other._basePrice);
    }
}
