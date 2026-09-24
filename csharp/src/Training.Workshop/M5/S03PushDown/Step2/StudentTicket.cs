using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step2;

/// <summary>Krok 2: bez zmian.</summary>
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
