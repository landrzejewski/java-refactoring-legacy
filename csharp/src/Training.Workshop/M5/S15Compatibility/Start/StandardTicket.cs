using Training.Workshop.M5.S15Compatibility;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Start;

/// <summary>Start: [Column("cena")] zadeklarowane w podklasie.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    [Column("cena")]
    public Money Price()
    {
        return BasePrice;
    }
}
