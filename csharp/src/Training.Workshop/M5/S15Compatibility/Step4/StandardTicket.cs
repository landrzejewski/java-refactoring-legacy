using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step4;

/// <summary>Krok 4: bez zmian.</summary>
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
