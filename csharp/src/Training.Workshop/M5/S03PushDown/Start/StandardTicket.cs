using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Start;

/// <summary>Start: jedyny bilet, który naprawdę korzysta z dopłaty VIP.</summary>
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
