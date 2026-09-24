using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>
/// Krok 3: kontekst dostaje strategię w konstruktorze i nie zna nazw programów.
/// Uwaga: wybór w konstruktorze zamraża decyzję - zmienia moment, w którym pada błąd nieznanego programu.
/// </summary>
public sealed class TicketPricer
{
    private readonly IDiscountPolicy _policy;

    public TicketPricer(IDiscountPolicy policy)
    {
        ArgumentNullException.ThrowIfNull(policy);
        _policy = policy;
    }

    public Money Price(Money basePrice, string ticketType)
    {
        if (basePrice.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("base price must not be negative");
        }
        return basePrice.Minus(_policy.Discount(basePrice, ticketType));
    }
}
