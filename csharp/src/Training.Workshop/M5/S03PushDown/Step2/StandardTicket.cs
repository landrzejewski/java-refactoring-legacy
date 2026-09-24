using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step2;

/// <summary>Krok 2: dopłata VIP liczona w podklasie (override Surcharge()) - baza nie czyta już pola.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(Money basePrice) : base(basePrice)
    {
    }

    protected override int DiscountPercent()
    {
        return 0;
    }

    protected override Money Surcharge()
    {
        return IsVipUpgraded ? Money.Of("10.00") : Money.Zero;
    }
}
