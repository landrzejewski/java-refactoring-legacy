using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step2;

/// <summary>Krok 2: Price() odziedziczone z Ticket.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    protected override int DiscountPercent()
    {
        return 0;
    }
}
