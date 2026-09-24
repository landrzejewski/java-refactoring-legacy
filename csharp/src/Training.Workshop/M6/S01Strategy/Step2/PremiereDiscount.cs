using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step2;

/// <summary>Krok 2: premiera - brak zniżek (typ biletu nie jest nawet sprawdzany, jak w Start).</summary>
public sealed class PremiereDiscount : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType)
    {
        return Money.Zero;
    }
}
