using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>Krok 3: premiera - brak zniżek (typ biletu nie jest nawet sprawdzany, jak w Start).</summary>
public sealed class PremiereDiscount : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType)
    {
        return Money.Zero;
    }
}
