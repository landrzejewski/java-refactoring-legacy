using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Start;

/// <summary>Start: baza biletów bez ceny - każda podklasa deklaruje własne Price() z atrybutem [Column].</summary>
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
}
