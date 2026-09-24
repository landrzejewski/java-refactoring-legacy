using Training.Workshop.M5.S15Compatibility;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step3;

/// <summary>Krok 3: bez zmian.</summary>
public abstract class Ticket
{
    private readonly string _title;
    private readonly Money _basePrice;

    protected Ticket(string title, Money basePrice)
    {
        _title = title;
        _basePrice = basePrice;
    }

    public string Title => _title;

    public Money BasePrice => _basePrice;

    [Column("cena")]
    public Money Price()
    {
        return _basePrice.Minus(_basePrice.Percent(DiscountPercent()));
    }

    protected abstract int DiscountPercent();
}
