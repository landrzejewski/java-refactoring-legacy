using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>Krok 3: tydzień studenta - student 50%, pozostali jak w programie standardowym.</summary>
public sealed class StudentWeekDiscount : IDiscountPolicy
{
    private readonly IDiscountPolicy _fallback;

    public StudentWeekDiscount(IDiscountPolicy fallback)
    {
        _fallback = fallback;
    }

    public Money Discount(Money basePrice, string ticketType)
    {
        return ticketType == "S" ? basePrice.Percent(50) : _fallback.Discount(basePrice, ticketType);
    }
}
