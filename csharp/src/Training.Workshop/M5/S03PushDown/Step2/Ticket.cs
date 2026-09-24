using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step2;

/// <summary>
/// Krok 2: najpierw przenosimy zachowanie korzystające z pola. Baza liczy cenę z punktem
/// rozszerzenia <c>Surcharge()</c>; o dopłacie VIP decyduje już StandardTicket.
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
        return _basePrice.Minus(_basePrice.Percent(DiscountPercent())).Plus(Surcharge());
    }

    protected virtual Money Surcharge()
    {
        return Money.Zero;
    }

    protected abstract int DiscountPercent();
}
