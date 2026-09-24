using Training.Workshop.M5.S15Compatibility;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step2;

/// <summary>
/// Krok 2: Pull Members Up - Price() (z atrybutem [Column]) w bazie, różnica w haku DiscountPercent().
/// Typ deklarujący metody zmienił się na Ticket: stare wywołania StudentTicket.Price() dalej się wiążą
/// (CLR szuka metody w klasach bazowych), ale GetMethods(DeclaredOnly) na podklasie już jej nie widzi.
/// </summary>
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
