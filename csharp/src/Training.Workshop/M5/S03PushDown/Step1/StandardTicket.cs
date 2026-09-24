using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step1;

/// <summary>Krok 1: bez zmian.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(Money basePrice) : base(basePrice)
    {
    }

    protected override int DiscountPercent()
    {
        return 0;
    }
}
