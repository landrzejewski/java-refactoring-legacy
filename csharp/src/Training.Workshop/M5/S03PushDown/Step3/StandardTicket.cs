using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step3;

/// <summary>Krok 3: stan i operacja VIP żyją tam, gdzie mają sens.</summary>
public sealed class StandardTicket : Ticket
{
    private bool _vipUpgraded;

    public StandardTicket(Money basePrice) : base(basePrice)
    {
    }

    public void UpgradeToVip()
    {
        _vipUpgraded = true;
    }

    public bool IsVipUpgraded => _vipUpgraded;

    protected override int DiscountPercent()
    {
        return 0;
    }

    protected override Money Surcharge()
    {
        return IsVipUpgraded ? Money.Of("10.00") : Money.Zero;
    }
}
