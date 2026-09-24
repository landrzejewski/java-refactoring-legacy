using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Start;

/// <summary>Start: odziedziczył operację, której nie może wykonać - łamie kontrakt bazy.</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(Money basePrice) : base(basePrice)
    {
    }

    public override void UpgradeToVip()
    {
        throw new NotSupportedException("bilet ulgowy nie ma dopłaty VIP");
    }

    protected override int DiscountPercent()
    {
        return 25;
    }
}
