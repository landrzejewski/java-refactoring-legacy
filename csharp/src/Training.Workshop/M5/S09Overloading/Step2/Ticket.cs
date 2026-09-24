using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Equals(object) z override i GetHashCode (Generate Equals and GetHashCode).
/// Słowo override zamienia cichy błąd przeciążenia w błąd kompilacji. Porównujemy GetType(),
/// bo bilet studencki i normalny na ten sam film to różne pozycje.
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

    public override bool Equals(object? other)
    {
        return other != null && GetType() == other.GetType()
            && _title == ((Ticket)other)._title && _basePrice.Equals(((Ticket)other)._basePrice);
    }

    public override int GetHashCode()
    {
        return HashCode.Combine(_title, _basePrice);
    }
}
