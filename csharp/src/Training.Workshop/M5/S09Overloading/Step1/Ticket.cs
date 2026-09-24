using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step1;

/// <summary>
/// Krok 1: Replace Overloading with Overriding - zniżka to wirtualna właściwość <c>DiscountPercent</c>,
/// wybierana dynamicznie według klasy runtime obiektu. Pułapka Equals(Ticket) jeszcze zostaje.
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

    public virtual int DiscountPercent => 0;

    public bool Equals(Ticket other)
    {
        return _title == other._title && _basePrice.Equals(other._basePrice);
    }
}
