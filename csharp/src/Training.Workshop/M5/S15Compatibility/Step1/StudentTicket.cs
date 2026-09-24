using Training.Workshop.M5.S15Compatibility;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step1;

/// <summary>Krok 1: bez zmian.</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    [Column("cena")]
    public Money Price()
    {
        return BasePrice.Minus(BasePrice.Percent(25));
    }
}
