using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Push Members Down - UpgradeToVip(), IsVipUpgraded i pole _vipUpgraded
/// trafiły do StandardTicket. Baza obiecuje tylko to, co prawdziwe dla wszystkich biletów.
/// </summary>
public abstract class Ticket
{
    private readonly Money _basePrice;

    protected Ticket(Money basePrice)
    {
        _basePrice = basePrice;
    }

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
