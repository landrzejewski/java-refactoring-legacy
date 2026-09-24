using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Start;

/// <summary>
/// Start: baza obiecuje dopłatę VIP wszystkim biletom, choć sensowna jest tylko dla biletu normalnego.
/// Sygnały: override rzucający NotSupportedException i klient sprawdzający typ przez <c>is</c>.
/// </summary>
public abstract class Ticket
{
    private readonly Money _basePrice;
    private bool _vipUpgraded;

    protected Ticket(Money basePrice)
    {
        _basePrice = basePrice;
    }

    public virtual void UpgradeToVip()
    {
        _vipUpgraded = true;
    }

    public bool IsVipUpgraded => _vipUpgraded;

    public Money Price()
    {
        var price = _basePrice.Minus(_basePrice.Percent(DiscountPercent()));
        return IsVipUpgraded ? price.Plus(Money.Of("10.00")) : price;
    }

    protected abstract int DiscountPercent();
}
